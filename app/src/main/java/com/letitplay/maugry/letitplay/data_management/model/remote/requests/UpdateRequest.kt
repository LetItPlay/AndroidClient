package com.letitplay.maugry.letitplay.data_management.model.remote.requests

import com.google.gson.annotations.SerializedName


open class UpdateRequest {
    @SerializedName("like_count")
    val likeCount: Byte
    @SerializedName("report_count")
    val reportCount: Byte
    @SerializedName("listen_count")
    val listenCount: Byte

    private constructor(likeDiff: Byte = 0, reportDiff: Byte = 0, listenDiff: Byte = 0) {
        likeCount = likeDiff
        reportCount = reportDiff
        listenCount = listenDiff
    }

    companion object {
        fun likeRequest() = UpdateRequest(likeDiff = 1)
        fun unlikeRequest() = UpdateRequest(likeDiff = -1)
        fun listenRequest() = UpdateRequest(listenDiff = 1)
    }
}