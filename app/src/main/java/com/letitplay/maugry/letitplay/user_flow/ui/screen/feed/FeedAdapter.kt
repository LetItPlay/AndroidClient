package com.letitplay.maugry.letitplay.user_flow.ui.screen.feed

//class FeedAdapter(
//        private val musicService: MusicService? = null,
//        private val onClickItem: ((ExtendTrackModel, Int) -> Unit)? = null,
//        private val onLikeClick: ((ExtendTrackModel, Boolean, Int) -> Unit)? = null,
//        private val playlistActionsListener: OnPlaylistActionsListener? = null
//) : RecyclerView.Adapter<FeedItemViewHolder>() {
//
//    var data: List<ExtendTrackModel> = ArrayList()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun onBindViewHolder(holder: FeedItemViewHolder?, position: Int) {
//        holder?.update(data[position])
//    }
//
//    override fun getItemCount(): Int = data.size
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FeedItemViewHolder {
//        return FeedItemViewHolder(
//                parent,
//                playlistActionsListener,
//                onClickItem,
//                onLikeClick,
//                musicService
//        )
//    }
//}