package com.letitplay.maugry.letitplay.user_flow.ui.screen.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_select_language.*

class LanguageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_language)
        eng_button.setOnClickListener {
            saveLang(Language.EN)
        }
        rus_button.setOnClickListener {
            saveLang(Language.RU)
        }

    }

    private fun saveLang(lang: Language) {
        this.let {
            PreferenceHelper(it).contentLanguage = lang
            it.startActivity(Intent(it, NavigationActivity::class.java))
        }
    }

}
