package android.arch.paging;


import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;

public class ShiftedListUpdateCallback implements ListUpdateCallback {
    private final RecyclerView.Adapter mAdapter;

    public ShiftedListUpdateCallback(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onInserted(int position, int count) {
        mAdapter.notifyItemRangeInserted(position+1, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        mAdapter.notifyItemRangeRemoved(position + 1, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        mAdapter.notifyItemMoved(fromPosition+1, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        mAdapter.notifyItemRangeChanged(position+1, count, payload);
    }
}
