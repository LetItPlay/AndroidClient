package com.letitplay.maugry.letitplay.data_management.repo

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList


data class Listing<T>(
        val pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>,
        val refresh: () -> Unit,
        val retry: () -> Unit
)