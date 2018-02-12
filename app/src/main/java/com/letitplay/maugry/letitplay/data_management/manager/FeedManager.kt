package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.service.ServiceController


object FeedManager : BaseManager() {

    fun getFeed(stIds: String, limit: Int, lang: String) =
            ServiceController.getFeed(stIds, limit, lang)


    fun getTrends(lang: String) = ServiceController.getTrends(lang)

    fun getSearch(lang: String) = ServiceController.getSearch(lang)
}