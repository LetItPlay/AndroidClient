package com.letitplay.maugry.letitplay.user_flow.business.profile

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.track_item.view.*


class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileItemHolder>() {

    var data: List<TrackModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ProfileItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClick?.invoke() }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProfileItemHolder = ProfileItemHolder(parent)

    class ProfileItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: TrackModel) {
            itemView.apply {
                track_last_seen.text = DataHelper.getData(track.publishedAt!!, context)
                //track_listen_count.text = track.listenCount.toString()
                track_time.text = DataHelper.getTime(track.audio?.lengthInSeconds)
                track_name.text = track.name
                Glide.with(context)
                        .load("${GL_MEDIA_SERVICE_URL}${track.image}")
                        .into(track_logo)
            }

        }

    }
}