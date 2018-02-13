package com.letitplay.maugry.letitplay.data_management.api.requests

import com.google.gson.annotations.SerializedName


open class UpdateRequestBody private constructor(likeDiff: Byte = 0, reportDiff: Byte = 0, listenDiff: Byte = 0) {
    @SerializedName("like_count")
    val likeCount: Byte = likeDiff
    @SerializedName("report_count")
    val reportCount: Byte = reportDiff
    @SerializedName("listen_count")
    val listenCount: Byte = listenDiff

    companion object {
        val LIKE = UpdateRequestBody(likeDiff = 1)
        val UNLIKE = UpdateRequestBody(likeDiff = -1)
        val LISTEN = UpdateRequestBody(listenDiff = 1)
    }
}