package com.letitplay.maugry.letitplay.data_management.model.remote.requests

import com.google.gson.annotations.SerializedName


open class UpdateFollowersRequestBody {
    @SerializedName("subscription_count")
    var subscriptionCount: Int

    private constructor(subscriptionDiff: Int = 0) {
        subscriptionCount = subscriptionDiff
    }

    companion object {
        fun buildFollowRequest() = UpdateFollowersRequestBody(subscriptionDiff = 1)
        fun buildUnFollowRequest() = UpdateFollowersRequestBody(subscriptionDiff = -1)
    }
}