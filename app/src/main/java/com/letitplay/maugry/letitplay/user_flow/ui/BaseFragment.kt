package com.letitplay.maugry.letitplay.user_flow.ui

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.GL_PROGRESS_DELAY
import com.letitplay.maugry.letitplay.user_flow.business.BasePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.widget.ProgressView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

abstract class BaseFragment<out P>(open val layoutId: Int,
                                   open val presenter: P? = null) : Fragment(), IMvpView
        where P : BasePresenter<IMvpView> {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    var isFragmentDestroying: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFragmentDestroying = false
        presenter?.apply(this)
    }

    fun getKey(): Int = arguments?.getInt("KEY") ?: -1

    override val name: String
        get() = this.toString()

    override val ctx: Context?
        get() = context

    override val viewFragmentManager: FragmentManager?
        get() = fragmentManager

    override val isViewDestroying: Boolean
        get() = activity?.isFinishing ?: true

    override val isViewDestroyed: Boolean
        get() = activity?.isDestroyed ?: true

    override val safeView: IMvpView?
        get() = if (!isViewDestroying && !isViewDestroyed && view != null && !isFragmentDestroying) this else null

    override fun onDestroyView() {
        // note: whatever called first or skipped
        isFragmentDestroying = true
        super.onDestroyView()
    }

    override fun onDestroy() {
        // note: whatever called first or skipped
        isFragmentDestroying = true
        super.onDestroy()
    }

    private var progress: ProgressView? = null

    override fun showProgress() {
        if (progress == null)
            progress = ProgressView(context).also { progress ->
                (view as? ConstraintLayout)?.let { parent ->
                    parent.addView(progress)
                    ConstraintSet().apply {
                        connect(progress.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                        connect(progress.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
                        connect(progress.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                        connect(progress.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                        constrainWidth(progress.id, ConstraintSet.MATCH_CONSTRAINT)
                        constrainHeight(progress.id, ConstraintSet.MATCH_CONSTRAINT)
                        applyTo(parent)
                    }
                }
            }
    }

    override fun hideProgress() {
        if (progress != null)
            Observable
                    .create<Unit> {
                        (view as? ViewGroup)?.removeView(progress)
                        progress = null
                        it.onComplete()
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delaySubscription(GL_PROGRESS_DELAY, TimeUnit.MILLISECONDS)
                    .subscribe({}, {}, {})
    }

    override fun restart() {

    }

    override fun destroy() {

    }
}
