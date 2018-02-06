package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.data_management.model.ContentLanguage
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfileAdapter
import com.letitplay.maugry.letitplay.user_flow.business.profile.ProfilePresenter
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.NavigationActivity
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : BaseFragment<ProfilePresenter>(R.layout.profile_fragment, ProfilePresenter) {

    private lateinit var profileListAdapter: ProfileAdapter
    private var profileRepo: MusicRepo? = null

    private val prefHelper: PreferenceHelper?
            get() = context?.let { PreferenceHelper(it) }

    private val currentContentLanguage: ContentLanguage?
            get() = prefHelper?.contentLanguage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val profileRecycler = view.findViewById<RecyclerView>(R.id.profile_list)
        profileListAdapter = ProfileAdapter(musicService, ::playTrack)
        profileRecycler.adapter = profileListAdapter
        profileRecycler.layoutManager = LinearLayoutManager(context)
        val divider = listDivider(profileRecycler.context, R.drawable.list_divider)
        profileRecycler.addItemDecoration(divider)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profile_header.attachTo(profile_list)
        presenter?.loadFavouriteTracks {
            presenter.extendTrackList?.let {
                profile_track_count.text = it.count().toString()
                profile_tracks_time.text = DateHelper.getTime(it.sumBy { it.track?.totalLengthInSeconds ?: 0 })
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