package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_select_language.*

class SelectLanguageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_select_language, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eng_button.setOnClickListener {
            saveLang(ContentLanguage.EN)
        }
        rus_button.setOnClickListener {
            saveLang(ContentLanguage.RU)
        }
    }

    private fun saveLang(lang: ContentLanguage) {
        context?.let {
            PreferenceHelper(it).contentLanguage = lang
            it.startActivity(Intent(it, NavigationActivity::class.java))
        }
    }
}