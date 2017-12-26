package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.queryAll


object TrackManager : BaseManager() {

    fun getTracks() = get(
            local = { TrackModel().queryAll() }
    )
}