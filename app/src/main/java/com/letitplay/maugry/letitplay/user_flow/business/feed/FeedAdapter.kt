package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.feed_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedChannelsItemHolder>() {

    var data: List<Pair<ChannelModel, TrackModel>> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClickItem: ((Long) -> Unit)? = null


    override fun onBindViewHolder(holder: FeedChannelsItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClickItem?.invoke(data[position].second.id!!) }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedChannelsItemHolder = FeedChannelsItemHolder(parent)

    class FeedChannelsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun getTime(data: String?, ctx: Context): String {
            var seconds = 0
            var minutes = 0
            var hours = 0
            var days = 0
            var months = 0
            var years = 0
            data?.let {
                val currentTime = System.currentTimeMillis()
                val publishDate: Date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(data)
                val milliseconds = currentTime - publishDate.time
                seconds = Math.round(milliseconds / 1000.0).toInt()
                minutes = Math.round(seconds / 60.0).toInt()
                hours = Math.round(minutes / 60.0).toInt()
                days = Math.round(hours / 24.0).toInt()
                months = Math.round(days / 30.42).toInt()
                years = Math.round(days / 365.5).toInt()
            }

            if (years != 0) return years.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (months != 0) return months.toString() + " " + ctx.resources.getString(R.string.feed_months)
            if (days != 0) return days.toString() + " " + ctx.resources.getString(R.string.feed_days)
            if (hours != 0) return hours.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (minutes != 0) return minutes.toString() + " " + ctx.resources.getString(R.string.feed_years)
            else return seconds.toString()
        }

        fun update(pair: Pair<ChannelModel, TrackModel>) {
            itemView.apply {
                val data = getTime(pair.second.published_at, context)
                feed_like.text = pair.second.like_count.toString()
                feed_time.text = pair.second.audio_file?.length_seconds.toString()
                feed_track_title.text = pair.second.name
                feed_channel_title.text = pair.first.name
                feed_track_last_update.text = data
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.first.image}")
                        .into(feed_channel_logo)
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.second.image}")
                        .into(feed_track_image)

            }
        }
    }
}