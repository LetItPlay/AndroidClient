package com.letitplay.maugry.letitplay.data_management.repo.compilation

import com.letitplay.maugry.letitplay.SchedulerProvider
import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.model.CompilationModel
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import io.reactivex.Single


class CompilationNetworkRepository(
        private val api: LetItPlayApi,
        private val preferenceHelper: PreferenceHelper,
        private val schedulerProvider: SchedulerProvider
): CompilationRepository {
    override fun getCompilations(): Single<List<CompilationModel>> {
        return api.getCompilation(preferenceHelper.contentLanguage!!.strValue)
                .map {
                    val (newsCompilationTitle, newsCompilationSubtitle) = when (preferenceHelper.contentLanguage) {
                        Language.RU -> "Актуальные новости за 30 минут" to "Подборка актуальных новостей в виде 30-минутного плейлиста"
                        else -> "Fresh news in 30 minutes" to "A compilation of fresh news in one 30-minute playlist"
                    }
                    listOf(CompilationModel(newsCompilationTitle, newsCompilationSubtitle, it.tracks!!, it.channels!!))
                }
                .subscribeOn(schedulerProvider.io())
    }
}