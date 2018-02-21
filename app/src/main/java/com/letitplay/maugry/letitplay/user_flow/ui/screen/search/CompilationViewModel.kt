package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

import android.arch.lifecycle.ViewModel
import com.letitplay.maugry.letitplay.data_management.repo.CompilationRepository
import com.letitplay.maugry.letitplay.utils.ext.toLiveData


class CompilationViewModel(
    private val compilationRepository: CompilationRepository
): ViewModel() {

    val compilations by lazy {
        compilationRepository.getCompilations()
                .toFlowable()
                .toLiveData()
    }
}