package com.letitplay.maugry.letitplay.user_flow.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast
import com.letitplay.maugry.letitplay.GL_DEEP_LINK_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import kotlinx.android.synthetic.main.view_track_dialog.view.*
import timber.log.Timber

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


    fun doReport(ctx: Context, trackId: Int, reason: Int) {
        ServiceLocator.trackRepository.report(trackId, reason)
                .doOnComplete {
                    Toast.makeText(ctx, R.string.report_message, Toast.LENGTH_LONG).show()
                }
                .subscribe({}, {
                    Timber.e(it, "Error when liking")
                })
    }

    fun showTrackContextMenu(ctx: Context,
                             trackTitle: String?, channelTitle: String?,
                             trackId: Int, channelId: Int?,
                             onOtherClick: (Int, Int) -> Unit) {
        BottomSheetDialog(ctx).apply {
            val dialogView = layoutInflater.inflate(R.layout.view_track_dialog, null)
            dialogView.share_button.setOnClickListener {
                SharedHelper.onOtherClick(context, trackTitle, channelTitle, trackId, channelId)
                this.dismiss()
            }
            dialogView.report_button.setOnClickListener {
                AlertDialog.Builder(ctx).apply {
                    setTitle(R.string.report_dialog_title)
                    setItems(R.array.report_reason, object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            when (p1) {
                                0 -> doReport(ctx, trackId, p1)
                                1 -> doReport(ctx, trackId, p1)
                                2 -> doReport(ctx, trackId, p1)
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