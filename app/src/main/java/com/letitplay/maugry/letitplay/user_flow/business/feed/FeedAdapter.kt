package com.letitplay.maugry.letitplay.user_flow.business.feed

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.letitplay.maugry.letitplay.GL_MEDIA_SERVICE_URL
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ChannelModel
import com.letitplay.maugry.letitplay.data_management.model.FavouriteTracksModel
import com.letitplay.maugry.letitplay.data_management.model.TrackModel
import com.letitplay.maugry.letitplay.data_management.repo.query
import com.letitplay.maugry.letitplay.data_management.repo.save
import com.letitplay.maugry.letitplay.user_flow.business.BaseViewHolder
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DataHelper
import kotlinx.android.synthetic.main.feed_item.view.*
import java.util.*

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.FeedChannelsItemHolder>() {

    var data: List<Pair<ChannelModel, TrackModel>> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onClickItem: ((Long) -> Unit)? = null
    var onLikeClick: ((Long?, Boolean) -> Unit)? = null

    override fun onBindViewHolder(holder: FeedChannelsItemHolder?, position: Int) {
        holder?.apply {
            var likeModel: FavouriteTracksModel? = FavouriteTracksModel().query { it.equalTo("id", data[position].second.id) }.firstOrNull()
            if (likeModel == null) {
                likeModel = FavouriteTracksModel(data[position].second.id, data[position].second.likeCount, false)
                likeModel.save()
            }
            update(data[position], likeModel)
            itemView.setOnClickListener { onClickItem?.invoke(data[position].second.id!!) }
            itemView.feed_like.setOnClickListener {
                onLikeClick?.invoke(data[position].second.id, it.feed_like.isLiked())
            }
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedChannelsItemHolder = FeedChannelsItemHolder(parent)

    class FeedChannelsItemHolder(parent: ViewGroup?) : BaseViewHolder(parent, R.layout.feed_item) {

        fun update(pair: Pair<ChannelModel, TrackModel>, like: FavouriteTracksModel?) {
            itemView.apply {
                val data = DataHelper.getData(pair.second.publishedAt!!, context)
                feed_like.like = like
                feed_time.text = DataHelper.getTime(pair.second.audio?.lengthInSeconds)
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