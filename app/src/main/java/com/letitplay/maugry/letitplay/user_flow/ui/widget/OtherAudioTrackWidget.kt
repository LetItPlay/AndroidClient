package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.gsfoxpro.musicservice.model.AudioTrack
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SharedHelper
import kotlinx.android.synthetic.main.other_audio_track_widget.view.*
import kotlinx.android.synthetic.main.view_track_dialog.view.*

class OtherAudioTrackWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var report: ((Int, Int) -> Unit)
    lateinit var track: AudioTrack

    init {
        LayoutInflater.from(context).inflate(R.layout.other_audio_track_widget, this)
        other_audio_track.setOnClickListener {
            showTrackContextMenu(track, report)
        }
    }

    fun showTrackContextMenu(track: AudioTrack,
                             report: ((Int, Int) -> Unit)) {
        BottomSheetDialog(context).apply {
            val dialogView = layoutInflater.inflate(R.layout.view_track_dialog, null)
            dialogView.share_button.setOnClickListener {
                SharedHelper.trackShare(context, track.title, track.channelTitle, track.id, track.channelId)
                this.dismiss()
            }
            dialogView.report_button.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.report_dialog_title)
                    setItems(R.array.report_reason, object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            when (p1) {
                                0 -> report(track.id, p1)
                                1 -> report(track.id, p1)
                                2 -> report(track.id, p1)
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