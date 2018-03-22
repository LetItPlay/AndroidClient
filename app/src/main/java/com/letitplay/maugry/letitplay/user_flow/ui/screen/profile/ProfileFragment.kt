package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.Optional
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.view_language_dialog.view.*


class ProfileFragment : BaseFragment(R.layout.profile_fragment) {

    private val likedTracksListAdapter: LikedTracksAdapter by lazy {
        LikedTracksAdapter(musicService, ::playTrack)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ProfileViewModel::class.java)
    }

    private var profileRepo: MusicRepo? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.likedTracks.observe(this, Observer<List<TrackWithChannel>> {

            profile_track_count.text = it?.count()?.toString() ?: "0"
            profile_tracks_time.text = DateHelper.getTime(it?.sumBy { it.track.totalLengthInSeconds } ?: 0)

            it?.let {
                likedTracksListAdapter.data = it
            }
        })
        vm.language.observe(this, Observer<Optional<Language>> {
            val lang = it?.value
            lang?.let {
                profile_current_language.text = getString(when (it) {
                    Language.RU -> R.string.language_ru
                    Language.EN -> R.string.language_en
                    Language.FR -> R.string.language_fr
                })
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

        change_content_language.setOnClickListener {
            BottomSheetDialog(requireContext()).apply {
                val dialogView = layoutInflater.inflate(R.layout.view_language_dialog, null)
                val onLanguageClick = View.OnClickListener { view ->
                    vm.changeLanguage((view as TextView).tag as String)
                    this.dismiss()
                }
                dialogView.rus_button.setOnClickListener(onLanguageClick)
                dialogView.eng_button.setOnClickListener(onLanguageClick)
                dialogView.fr_button.setOnClickListener(onLanguageClick)
                setContentView(dialogView)
                show()
            }
        }
    }

    private fun playTrack(track: Track) {
        val trackId = track.id
        if (profileRepo != null && profileRepo?.getAudioTrackAtId(trackId) != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(track.id)
            return
        }
        vm.likedTracks.value?.let {
            profileRepo = MusicRepo(it.map(TrackWithChannel::toAudioTrack).toMutableList())
        }
        navigationActivity.updateRepo(track.id, profileRepo)
    }
}