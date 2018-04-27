package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat.startActivity
import com.letitplay.maugry.letitplay.GL_DEEP_LINK_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import kotlinx.android.synthetic.main.view_track_dialog.view.*

object SharedHelper {

    private fun getChannelUrl(title: String?, id: Int?): String = "$title:  ${GL_DEEP_LINK_SERVICE_URL}channel=$id"

    private fun getTrackUrl(trackTitle: String?, channelTitle: String?, trackId: Int?, channelId: Int?) = "$channelTitle-$trackTitle:  ${GL_DEEP_LINK_SERVICE_URL}channel=$channelId&track=$trackId"

    private fun onOtherClick(ctx: Context, trackTitle: String?, channelTitle: String?, trackId: Int?, channelId: Int?) {
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


    fun showTrackContextMenu(ctx: Context, track: TrackWithChannel,
                             onOtherClick: (TrackWithChannel, Int) -> Unit) {
        BottomSheetDialog(ctx).apply {
            val dialogView = layoutInflater.inflate(R.layout.view_track_dialog, null)
            dialogView.share_button.setOnClickListener {
                SharedHelper.onOtherClick(context, track.track.title, track.channel.name, track.track.id, track.channel.id)
                this.dismiss()
            }
            dialogView.report_button.setOnClickListener {
                AlertDialog.Builder(ctx).apply {
                    setTitle("Причина")
                    setItems(R.array.report_reason, object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            when (p1) {
                                0 -> onOtherClick(track, 0)
                                1 -> onOtherClick(track, 1)
                                2 -> onOtherClick(track, 2)
                            }
                        }

                    })
                }.create().show()
                this.dismiss()
            }
            setContentView(dialogView)
            show()
        }
    }

}