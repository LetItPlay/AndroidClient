package com.letitplay.maugry.letitplay.user_flow.business

import android.content.Context
import com.letitplay.maugry.letitplay.user_flow.ui.IMvpView
import io.reactivex.Observable
import java.lang.ref.WeakReference

class ExecutionConfig<O, V>(
        /**  cache   */
        val cache: O? = null,
        val hasValidCache: (() -> Boolean)? = { cache != null },

        /**  providers   */
        val asyncObservable: Observable<O>? = null,

        /*      actions         */
        val onNextNonContext: ((O) -> Unit)? = null,
        val onCompleteNonContext: (() -> Unit)? = null,
        val onNextWithContext: ((V?, O) -> Unit)? = null,
        val onCompleteWithContext: ((V?) -> Unit)? = null,
        val onErrorWithContext: ((V?, Throwable) -> Unit)? = null,

        /**  view stuff */
        val triggerProgress: Boolean = true,

        /**  replay subject conf */
        val replaySize: Int = 1,
        val replayTime: Long? = null
)
        where V : IMvpView {

    /**
     *       FOR SYS ACCESS ONLY
     * */
    var viewRef: WeakReference<V?> = WeakReference(null)
    @Suppress("UNCHECKED_CAST")
    inline val mvp
        get() = viewRef.get()?.safeView as? V?
    inline val context: Context? get() = mvp?.ctx
    inline val isAlive get() = context != null
}