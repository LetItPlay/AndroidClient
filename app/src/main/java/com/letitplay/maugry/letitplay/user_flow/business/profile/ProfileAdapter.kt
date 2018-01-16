package com.letitplay.maugry.letitplay.user_flow.business.profile

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import com.letitplay.maugry.letitplay.utils.loadImage
import kotlinx.android.synthetic.main.track_item.view.*


class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileItemHolder>() {

    var data: List<TrackModel> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClick: (() -> Unit)? = null

    override fun onBindViewHolder(holder: ProfileItemHolder?, position: Int) {
        holder?.update(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ProfileItemHolder {
        return ProfileItemHolder(parent).apply {
            itemView.setOnClickListener {
                onClick?.invoke()
            }
        }
    }

    class ProfileItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.track_item) {

        fun update(track: TrackModel) {
            itemView.apply {
                track_last_seen.text = DataHelper.getData(track.publishedAt!!, context)
                //track_listen_count.text = track.listenCount.toString()
                track_time.text = DataHelper.getTime(track.audio?.lengthInSeconds)
                track_name.text = track.name
                track_logo.loadImage(context, track.image)
            }

        }

    }
}