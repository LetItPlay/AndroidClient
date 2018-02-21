package com.letitplay.maugry.letitplay.data_management.repo.search

import com.letitplay.maugry.letitplay.data_management.model.SearchResultItem
import io.reactivex.Flowable


interface SearchRepository {
    fun performQuery(query: String): Flowable<List<SearchResultItem>>
}