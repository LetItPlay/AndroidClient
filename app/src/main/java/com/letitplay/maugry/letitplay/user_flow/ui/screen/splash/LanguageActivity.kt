package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_select_language.*

class LanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_language)
        eng_button.setOnClickListener {
            saveLang(ContentLanguage.EN)
        }
        rus_button.setOnClickListener {
            saveLang(ContentLanguage.RU)
        }

    }

    private fun saveLang(lang: ContentLanguage) {
        this.let {
            PreferenceHelper(it).contentLanguage = lang
            it.startActivity(Intent(it, NavigationActivity::class.java))
        }
    }

}
