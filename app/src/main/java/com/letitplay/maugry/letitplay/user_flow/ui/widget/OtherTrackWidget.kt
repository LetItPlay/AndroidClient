package com.letitplay.maugry.letitplay.user_flow.ui.widget

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialog
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.utils.SharedHelper
import kotlinx.android.synthetic.main.other_track_widget_layout.view.*
import kotlinx.android.synthetic.main.view_track_dialog.view.*

class OtherTrackWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {


    lateinit var report: ((Int, Int) -> Unit)
    lateinit var track: TrackWithChannel

    init {
        LayoutInflater.from(context).inflate(R.layout.other_track_widget_layout, this)
        other.setOnClickListener {
            showTrackContextMenu(track, report)
        }
    }

    fun showTrackContextMenu(track: TrackWithChannel,
                             report: ((Int, Int) -> Unit)) {
        BottomSheetDialog(context).apply {
            val dialogView = layoutInflater.inflate(R.layout.view_track_dialog, null)
            dialogView.share_button.setOnClickListener {
                SharedHelper.trackShare(context, track.track.title, track.channel.name, track.track.id, track.channel.id)
                this.dismiss()
            }
            dialogView.report_button.setOnClickListener {
                AlertDialog.Builder(context).apply {
                    setTitle(R.string.report_dialog_title)
                    setItems(R.array.report_reason, object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            when (p1) {
                                0 -> report(track.track.id, p1)
                                1 -> report(track.track.id, p1)
                                2 -> report(track.track.id, p1)
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