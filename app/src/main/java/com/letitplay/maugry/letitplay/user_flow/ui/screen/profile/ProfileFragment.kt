package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment(R.layout.profile_fragment) {

    private val likedTracksListAdapter: LikedTracksAdapter by lazy {
        LikedTracksAdapter(musicService, ::playTrack)
    }

    private val vm by lazy {
        ViewModelProviders.of(this, ServiceLocator.viewModelFactory)
                .get(ProfileViewModel::class.java)
    }

    private var profileRepo: MusicRepo? = null

    private val prefHelper: PreferenceHelper?
        get() = context?.let { PreferenceHelper(it) }

    private val currentContentLanguage: Language?
        get() = prefHelper?.contentLanguage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.likedTracks.observe(this, Observer<List<TrackWithChannel>> {

            profile_track_count.text = it?.count()?.toString() ?: "0"
            profile_tracks_time.text = DateHelper.getTime(it?.sumBy { it.track.totalLengthInSeconds } ?: 0)

            it?.let {
                likedTracksListAdapter.data = it
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val profileRecycler = view.findViewById<RecyclerView>(R.id.profile_list)
        profileRecycler.adapter = likedTracksListAdapter
        val divider = listDivider(profileRecycler.context, R.drawable.list_divider)
        profileRecycler.addItemDecoration(divider)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_header.attachTo(profile_list)

        val switchToRu = getString(R.string.profile_change_language_to_ru)
        val switchToEn = getString(R.string.profile_change_language_to_en)

        change_content_language_text.text = if (currentContentLanguage == Language.EN) switchToRu else switchToEn

        change_content_language.setOnClickListener {
            prefHelper?.contentLanguage = if (currentContentLanguage == Language.EN) Language.RU else Language.EN
            change_content_language_text.text = if (currentContentLanguage == Language.EN) switchToRu else switchToEn
        }
    }

    private fun playTrack(track: Track) {
        if (profileRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
//        presenter?.playlist?.let {
//            profileRepo = MusicRepo(it)
//        }
        navigationActivity.updateRepo(track.id, profileRepo)
    }
}