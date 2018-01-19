package com.letitplay.maugry.letitplay.user_flow.business.profile

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.gsfoxpro.musicservice.service.MusicService
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ExtendTrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.track_item.view.*


class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileItemHolder>() {

    var data: List<ExtendTrackModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var musicService: MusicService? = null
    var onClickItem: ((Long) -> Unit)? = null

    override fun onBindViewHolder(holder: ProfileItemHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProfileItemHolder {
        return ProfileItemHolder(parent).apply {
            itemView.track_playing_now.mediaSession = musicService?.mediaSession
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickItem?.invoke(data[adapterPosition].track?.id!!)
                }
            }
        }
    }

    class ProfileItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(extendTrack: ExtendTrackModel) {
            itemView.apply {
                track_last_seen.text = DateHelper.getLongPastDate(extendTrack.track?.publishedAt, context)
                track_playing_now.trackListenerCount = extendTrack.track?.listenCount
                track_playing_now.trackUrl = "${GL_MEDIA_SERVICE_URL}${extendTrack.track?.audio?.fileUrl}"
                channel_name.text = extendTrack.channel?.name
                track_time.text = DateHelper.getTime(extendTrack.track?.audio?.lengthInSeconds)
                track_name.text = extendTrack.track?.name
                track_logo.loadImage(extendTrack.track?.image)
            }
        }
    }
}