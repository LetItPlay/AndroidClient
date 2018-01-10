package com.letitplay.maugry.letitplay.data_management.model

import com.google.gson.annotations.SerializedName


open class FollowersModel(
        @SerializedName("subscription_count")
        var subscriptionCount: Int? = null
)