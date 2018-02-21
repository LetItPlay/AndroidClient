package com.letitplay.maugry.letitplay.data_management.repo.compilation

import com.letitplay.maugry.letitplay.data_management.model.CompilationModel
import io.reactivex.Single


interface CompilationRepository {
    fun getCompilations(): Single<List<CompilationModel>>
}