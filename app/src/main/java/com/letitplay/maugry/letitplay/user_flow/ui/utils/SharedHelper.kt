package com.letitplay.maugry.letitplay.user_flow.ui.utils

import com.letitplay.maugry.letitplay.GL_DATA_SERVICE_URL

object SharedHelper {

    fun getChannelUrl(title: String?, id: Int?): String = "$title:  ${GL_DATA_SERVICE_URL}stations/$id"

    fun getTrackUrl(trackTitle: String?, channelTitle: String?, id: Int?) = "$channelTitle-$trackTitle:  ${GL_DATA_SERVICE_URL}tracks/$id"

}