package com.letitplay.maugry.letitplay.data_management.repo

import com.letitplay.maugry.letitplay.data_management.api.LetItPlayApi


interface TrackRepository {

}

class TrackRepositoryImpl(
        private val api: LetItPlayApi
) : TrackRepository {
}