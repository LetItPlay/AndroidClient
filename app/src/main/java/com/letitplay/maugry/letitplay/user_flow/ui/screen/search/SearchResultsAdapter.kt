package com.letitplay.maugry.letitplay.user_flow.ui.screen.search

//typealias ChannelVH = SearchResultsAdapter.ChannelSmallViewHolder
//typealias TrackVH = TrackAdapter.TrackItemHolder
//
//sealed class ResultItem {
//    class ChannelItem(val channelItemModel: ExtendChannelModel) : ResultItem()
//    class TrackItem(val track: com.gsfoxpro.musicservice.model.AudioTrack) : ResultItem()
//}
//
//class SearchResultsAdapter(
//        private val musicService: MusicService?,
//        private val onChannelClick: ((Channel) -> Unit),
//        private val onTrackClick: ((AudioTrack) -> Unit),
//        private var onFollowClick: ((ExtendChannelModel, Boolean, Int) -> Unit)
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    var data: List<ResultItem> = emptyList()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (data[position]) {
//            is ResultItem.ChannelItem -> CHANNEL_ITEM_TYPE
//            is ResultItem.TrackItem -> TRACK_ITEM_TYPE
//        }
//    }
//
//    override fun getItemCount() = data.size
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            CHANNEL_ITEM_TYPE -> ChannelVH(parent).apply {
//                itemView.setOnClickListener {
//                    if (adapterPosition != NO_POSITION) {
//                        onChannelClick((data[adapterPosition] as ResultItem.ChannelItem).channelItemModel.channel!!)
//                    }
//                }
//                itemView.channel_follow.setOnClickListener {
//                    if (adapterPosition != NO_POSITION) {
//                        onFollowClick(
//                                (data[adapterPosition] as ResultItem.ChannelItem).channelItemModel,
//                                it.channel_follow.isFollow(),
//                                adapterPosition
//                        )
//                    }
//                }
//            }
//            TRACK_ITEM_TYPE -> TrackVH(parent).apply {
//                itemView.setOnClickListener {
//                    if (adapterPosition != NO_POSITION) {
//                        onTrackClick((data[adapterPosition] as ResultItem.TrackItem).track)
//                    }
//                }
//                itemView.track_playing_now.mediaSession = musicService?.mediaSession
//            }
//            else -> throw IllegalStateException()
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = data[position]
//        when {
//            holder is ChannelVH && item is ResultItem.ChannelItem -> holder.update(item.channelItemModel)
//            holder is TrackVH && item is ResultItem.TrackItem -> holder.update(item.track)
//        }
//    }
//
//    class ChannelSmallViewHolder(val parent: ViewGroup?) : BaseViewHolder(parent, R.layout.channels_item_small) {
//
//        fun update(channelItem: ExtendChannelModel) {
//            itemView.apply {
//                channel_name.text = channelItem.channel!!.name
//                channel_follow.data = channelItem.following
//                channel_followers_count.text = channelItem.channel!!.subscriptionCount.toString()
//                channel_small_logo.loadImage(channelItem.channel!!.imageUrl)
//            }
//        }
//    }
//
//    companion object {
//        const val CHANNEL_ITEM_TYPE = 1
//        const val TRACK_ITEM_TYPE = 2
//    }
//}