package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfileAdapter
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfilePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.utils.PreferenceHerlper
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment<ProfilePresenter>(R.layout.profile_fragment, ProfilePresenter) {

    private val profileListAdapter = ProfileAdapter()
    private var profileRepo: MusicRepo? = null

    private val prefHelper: PreferenceHerlper?
            get() = context?.let { PreferenceHerlper(it) }

    private val currentContentLanguage: ContentLanguage?
            get() = prefHelper?.contentLanguage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_list.apply {
            adapter = profileListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        profile_header.attachTo(profile_list)
        presenter?.loadFavouriteTracks {
            presenter.extendTrackList?.let {
                profile_track_count.text = it.count().toString()
                profile_tracks_time.text = DateHelper.getTime(it.sumBy { it.track?.audio?.lengthInSeconds ?: 0 })
                profileListAdapter.musicService = musicService
                profileListAdapter.onClickItem = { playTrack(it) }
                profileListAdapter.data = it
            }
        }

        val switchToRu = getString(R.string.profile_change_language_to_ru)
        val switchToEn = getString(R.string.profile_change_language_to_en)

        change_content_language_text.text = if (currentContentLanguage == ContentLanguage.EN) switchToRu else switchToEn

        change_content_language.setOnClickListener {
            prefHelper?.contentLanguage = if (currentContentLanguage == ContentLanguage.EN) ContentLanguage.RU else ContentLanguage.EN
            change_content_language_text.text = if (currentContentLanguage == ContentLanguage.EN) switchToRu else switchToEn
        }
    }

    private fun playTrack(trackId: Long) {
        if (profileRepo != null) {
            navigationActivity.musicPlayerSmall?.skipToQueueItem(trackId)
            return
        }
        presenter?.playlist?.let {
            profileRepo = MusicRepo(it)
        }
        (activity as NavigationActivity).updateRepo(trackId, profileRepo)
    }
}