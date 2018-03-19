package com.letitplay.maugry.letitplay.data_management.repo.userToken

import io.reactivex.Completable


interface UserTokenRepository {
    fun getJwt(userToken: String): Completable
}