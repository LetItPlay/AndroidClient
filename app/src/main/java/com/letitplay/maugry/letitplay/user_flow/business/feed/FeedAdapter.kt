package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import kotlinx.android.synthetic.main.feed_item.view.*
import java.util.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedChannelsItemHolder>() {

    var data: List<Pair<ChannelModel, TrackModel>> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClickItem: ((Long) -> Unit)? = null
    var onLikeClick: ((Long?) -> Unit)? = null

    override fun onBindViewHolder(holder: FeedChannelsItemHolder?, position: Int) {
        holder?.apply {
            update(data[position])
            itemView.setOnClickListener { onClickItem?.invoke(data[position].second.id!!) }
            itemView.like.setOnClickListener {
                onLikeClick?.invoke(data[position].second.id)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedChannelsItemHolder = FeedChannelsItemHolder(parent)

    class FeedChannelsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun getTime(date: Date, ctx: Context): String {
            val now = System.currentTimeMillis()
            val diff = Date(now - date.time)
            val cal = Calendar.getInstance()
            cal.time = diff
            val seconds = cal.get(Calendar.SECOND)
            val minutes = cal.get(Calendar.MINUTE)
            val hours = cal.get(Calendar.HOUR)
            val days = cal.get(Calendar.DAY_OF_MONTH)
            val months = cal.get(Calendar.MONTH)
            val years = cal.get(Calendar.YEAR) - 1970
            if (years != 0) return years.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (months != 0) return months.toString() + " " + ctx.resources.getString(R.string.feed_months)
            if (days != 0) return days.toString() + " " + ctx.resources.getString(R.string.feed_days)
            if (hours != 0) return hours.toString() + " " + ctx.resources.getString(R.string.feed_years)
            if (minutes != 0) return minutes.toString() + " " + ctx.resources.getString(R.string.feed_years)
            else return seconds.toString()
        }

        fun update(pair: Pair<ChannelModel, TrackModel>) {
            itemView.apply {
                val data = getTime(pair.second.publishedAt!!, context)
                feed_time.text = pair.second.audio?.lengthInSeconds.toString()
                feed_track_title.text = pair.second.name
                feed_channel_title.text = pair.first.name
                feed_track_last_update.text = data
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.first.imageUrl}")
                        .into(feed_channel_logo)
                Glide.with(context)
                        .load("$GL_MEDIA_SERVICE_URL${pair.second.image}")
                        .into(feed_track_image)

            }
        }
    }
}