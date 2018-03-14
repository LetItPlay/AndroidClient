package android.arch.paging;


import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.recyclerview.extensions.ListAdapterConfig;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

public class HackyPagedListAdapterHelper<T> {
    // updateCallback notifications must only be notified *after* new data and item count are stored
    // this ensures Adapter#notifyItemRangeInserted etc are accessing the new data
    private final ListUpdateCallback mUpdateCallback;
    private final ListAdapterConfig<T> mConfig;

    // TODO: REAL API
    interface PagedListListener<T> {
        void onCurrentListChanged(@Nullable PagedList<T> currentList);
    }

    @Nullable
    android.arch.paging.PagedListAdapterHelper.PagedListListener<T> mListener;

    private boolean mIsContiguous;

    private PagedList<T> mPagedList;
    private PagedList<T> mSnapshot;

    // Max generation of currently scheduled runnable
    private int mMaxScheduledGeneration;

    /**
     * Convenience for {@code PagedListAdapterHelper(new ListAdapterHelper.AdapterCallback(adapter),
     * new ListAdapterConfig.Builder<T>().setDiffCallback(diffCallback).build());
     *
     * @param adapter Adapter that will receive update signals.
     * @param diffCallback The {@link DiffCallback } instance to compare items in the list.
     */
    @SuppressWarnings("WeakerAccess")
    public HackyPagedListAdapterHelper(RecyclerView.Adapter adapter, DiffCallback<T> diffCallback) {
        mUpdateCallback = new ShiftedListUpdateCallback(adapter);
        mConfig = new ListAdapterConfig.Builder<T>().setDiffCallback(diffCallback).build();
    }

    private PagedList.Callback mPagedListCallback = new PagedList.Callback() {
        @Override
        public void onInserted(int position, int count) {
            mUpdateCallback.onInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            mUpdateCallback.onRemoved(position, count);
        }

        @Override
        public void onChanged(int position, int count) {
            // NOTE: pass a null payload to convey null -> item
            mUpdateCallback.onChanged(position, count, null);
        }
    };

    /**
     * Get the item from the current PagedList at the specified index.
     * <p>
     * Note that this operates on both loaded items and null padding within the PagedList.
     *
     * @param index Index of item to get, must be >= 0, and &lt; {@link #getItemCount()}.
     * @return The item, or null, if a null placeholder is at the specified position.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        if (mPagedList == null) {
            if (mSnapshot == null) {
                throw new IndexOutOfBoundsException(
                        "Item count is zero, getItem() call is invalid");
            } else {
                return mSnapshot.get(index);
            }
        }

        mPagedList.loadAround(index);
        return mPagedList.get(index);
    }

    /**
     * Get the number of items currently presented by this AdapterHelper. This value can be directly
     * returned to {@link RecyclerView.Adapter#getItemCount()}.
     *
     * @return Number of items being presented.
     */
    @SuppressWarnings("WeakerAccess")
    public int getItemCount() {
        if (mPagedList != null) {
            return mPagedList.size();
        }

        return mSnapshot == null ? 0 : mSnapshot.size();
    }

    /**
     * Pass a new PagedList to the AdapterHelper.
     * <p>
     * If a PagedList is already present, a diff will be computed asynchronously on a background
     * thread. When the diff is computed, it will be applied (dispatched to the
     * {@link ListUpdateCallback}), and the new PagedList will be swapped in.
     *
     * @param pagedList The new PagedList.
     */
    public void setList(final PagedList<T> pagedList) {
        if (pagedList != null) {
            if (mPagedList == null && mSnapshot == null) {
                mIsContiguous = pagedList.isContiguous();
            } else {
                if (pagedList.isContiguous() != mIsContiguous) {
                    throw new IllegalArgumentException("AdapterHelper cannot handle both contiguous"
                            + " and non-contiguous lists.");
                }
            }
        }

        if (pagedList == mPagedList) {
            // nothing to do
            return;
        }

        // incrementing generation means any currently-running diffs are discarded when they finish
        final int runGeneration = ++mMaxScheduledGeneration;

        if (pagedList == null) {
            int removedCount = getItemCount();
            if (mPagedList != null) {
                mPagedList.removeWeakCallback(mPagedListCallback);
                mPagedList = null;
            } else if (mSnapshot != null) {
                mSnapshot = null;
            }
            // dispatch update callback after updating mPagedList/mSnapshot
            mUpdateCallback.onRemoved(0, removedCount);
            if (mListener != null) {
                mListener.onCurrentListChanged(null);
            }
            return;
        }

        if (mPagedList == null && mSnapshot == null) {
            // fast simple first insert
            mPagedList = pagedList;
            pagedList.addWeakCallback(null, mPagedListCallback);

            // dispatch update callback after updating mPagedList/mSnapshot
            mUpdateCallback.onInserted(0, pagedList.size());

            if (mListener != null) {
                mListener.onCurrentListChanged(pagedList);
            }
            return;
        }

        if (mPagedList != null) {
            // first update scheduled on this list, so capture mPages as a snapshot, removing
            // callbacks so we don't have resolve updates against a moving target
            mPagedList.removeWeakCallback(mPagedListCallback);
            mSnapshot = (PagedList<T>) mPagedList.snapshot();
            mPagedList = null;
        }

        if (mSnapshot == null || mPagedList != null) {
            throw new IllegalStateException("must be in snapshot state to diff");
        }

        final PagedList<T> oldSnapshot = mSnapshot;
        final PagedList<T> newSnapshot = (PagedList<T>) pagedList.snapshot();
        mConfig.getBackgroundThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final DiffUtil.DiffResult result;
                result = PagedStorageDiffHelper.computeDiff(
                        oldSnapshot.mStorage,
                        newSnapshot.mStorage,
                        mConfig.getDiffCallback());

                mConfig.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mMaxScheduledGeneration == runGeneration) {
                            latchPagedList(pagedList, newSnapshot, result);
                        }
                    }
                });
            }
        });
    }

    private void latchPagedList(
            PagedList<T> newList, PagedList<T> diffSnapshot,
            DiffUtil.DiffResult diffResult) {
        if (mSnapshot == null || mPagedList != null) {
            throw new IllegalStateException("must be in snapshot state to apply diff");
        }

        PagedList<T> previousSnapshot = mSnapshot;
        mPagedList = newList;
        mSnapshot = null;

        // dispatch update callback after updating mPagedList/mSnapshot
        PagedStorageDiffHelper.dispatchDiff(mUpdateCallback,
                previousSnapshot.mStorage, newList.mStorage, diffResult);

        newList.addWeakCallback(diffSnapshot, mPagedListCallback);
        if (mListener != null) {
            mListener.onCurrentListChanged(mPagedList);
        }
    }

    /**
     * Returns the list currently being displayed by the AdapterHelper.
     * <p>
     * This is not necessarily the most recent list passed to {@link #setList(PagedList)}, because a
     * diff is computed asynchronously between the new list and the current list before updating the
     * currentList value.
     *
     * @return The list currently being displayed.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public PagedList<T> getCurrentList() {
        if (mSnapshot != null) {
            return mSnapshot;
        }
        return mPagedList;
    }
}

