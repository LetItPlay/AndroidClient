package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat.startActivity
import com.letitplay.maugry.letitplay.GL_DEEP_LINK_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import kotlinx.android.synthetic.main.view_track_dialog.*
import kotlinx.android.synthetic.main.view_track_dialog.view.*

object SharedHelper {

    fun getChannelUrl(title: String?, id: Int?): String = "$title:  ${GL_DEEP_LINK_SERVICE_URL}channel=$id"

    fun getTrackUrl(trackTitle: String?, channelTitle: String?, id: Int?) = "$channelTitle-$trackTitle:  ${GL_DEEP_LINK_SERVICE_URL}track=$id"

    fun onOtherClick(ctx: Context, trackTitle: String?, channelTitle: String?, trackId: Int?) {
        var sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, SharedHelper.getTrackUrl(trackTitle, channelTitle, trackId))
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


    fun showTrackContextMenu(ctx: Context, trackTitle: String?, channelTitle: String?, trackId: Int?) {
        BottomSheetDialog(ctx).apply {
            val dialogView = layoutInflater.inflate(R.layout.view_track_dialog, null)
            dialogView.share_button.setOnClickListener {
                SharedHelper.onOtherClick(context, trackTitle, channelTitle, trackId)
                this.dismiss()
            }
            dialogView.report_button.setOnClickListener {
                AlertDialog.Builder(ctx).apply {
                    setTitle("Причина")
                    setItems(arrayOf("Спам", "Порнография"), object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

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