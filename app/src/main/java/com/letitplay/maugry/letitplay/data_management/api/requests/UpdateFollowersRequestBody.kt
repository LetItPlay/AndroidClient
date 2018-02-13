package com.letitplay.maugry.letitplay.data_management.api.requests

import com.google.gson.annotations.SerializedName


open class UpdateFollowersRequestBody private constructor(subscriptionDiff: Int = 0) {
    @SerializedName("subscription_count")
    var subscriptionCount: Int = subscriptionDiff

    companion object {
        val FOLLOW = UpdateFollowersRequestBody(subscriptionDiff = 1)
        val UNFOLLOW = UpdateFollowersRequestBody(subscriptionDiff = -1)
    }
}