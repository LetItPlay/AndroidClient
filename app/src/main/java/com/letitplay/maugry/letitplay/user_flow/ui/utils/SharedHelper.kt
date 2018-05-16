package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.letitplay.maugry.letitplay.GL_DEEP_LINK_SERVICE_URL

object SharedHelper {

    fun getChannelUrl(title: String?, id: Int?): String = "$title:  ${GL_DEEP_LINK_SERVICE_URL}channel=$id"

    fun getTrackUrl(trackTitle: String?, channelTitle: String?, trackId: Int?, channelId: Int?) = "$channelTitle-$trackTitle:  ${GL_DEEP_LINK_SERVICE_URL}channel=$channelId&track=$trackId"

    fun trackShare(ctx: Context, trackTitle: String?, channelTitle: String?, trackId: Int?, channelId: Int?) {
        var sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, SharedHelper.getTrackUrl(trackTitle, channelTitle, trackId, channelId))
        sendIntent.type = "text/plain"
        startActivity(ctx, sendIntent, null)
    }

    fun channelShare(ctx: Context, channelTitle: String?, channelId: Int?) {
        var sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, SharedHelper.getChannelUrl(channelTitle, channelId))
        sendIntent.type = "text/plain"
        startActivity(ctx, sendIntent, null)
    }

}