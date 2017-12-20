package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

import android.os.Bundle
import android.view.View
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import kotlinx.android.synthetic.main.crash_test_fragment.*
import java.util.*

class FeedTestFragment : BaseFragment(R.layout.crash_test_fragment) {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
                /* val N: Int = Random().nextInt(6) +1
        when(N){
            1->test_img.setImageResource(R.drawable.cube)
            2->test_img.setImageResource(R.drawable.glass)
            3->test_img.setImageResource(R.drawable.kosmos)
            4->test_img.setImageResource(R.drawable.nature)
            5->test_img.setImageResource(R.drawable.ocean)
            6->test_img.setImageResource(R.drawable.tree)
            7->test_img.setImageResource(R.drawable.treugolnik)
        }*/
        test_img.setOnClickListener { goToOtherView() }

    }

    private fun goToOtherView() {
        (activity as NavigationActivity).navigateTo(FeedTestKey())
    }
}
