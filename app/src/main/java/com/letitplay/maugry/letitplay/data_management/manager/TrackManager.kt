package com.letitplay.maugry.letitplay.data_management.manager

import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.deleteAll
import com.letitplay.maugry.letitplay.data_management.repo.queryAll
import com.letitplay.maugry.letitplay.data_management.repo.saveAll
import com.letitplay.maugry.letitplay.data_management.service.ServiceController


object TrackManager : BaseManager() {

    fun getTracks(id: Int) = get(
            local = { TrackModel().queryAll() },
            remote = ServiceController.getTracks(id),
            remoteWhen = { true },
            update = { remote ->
                TrackModel().deleteAll()
                remote.saveAll()
            }
    )
}