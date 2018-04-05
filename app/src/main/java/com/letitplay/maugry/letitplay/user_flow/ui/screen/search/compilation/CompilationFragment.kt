package com.letitplay.maugry.letitplay.user_flow.ui.screen.search.compilation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.CompilationModel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.data_management.model.toTrackWithChannels
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchResultsKey
import com.letitplay.maugry.letitplay.utils.ext.show
import kotlinx.android.synthetic.main.compilation_fragment.*
import timber.log.Timber


class CompilationFragment : BaseFragment(R.layout.compilation_fragment) {

    private val compilationAdapter: CompilationAdapter by lazy {
        CompilationAdapter(::onCompilationClick)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(CompilationViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.compilations.observe(this, Observer<List<CompilationModel>> {
            when (it?.isEmpty()) {
                true -> compilation_no_recommendations.show()
                else -> compilationAdapter.data = it!!
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        val recycler = view.findViewById<RecyclerView>(R.id.compilation_list)
        recycler.adapter = compilationAdapter
        return view
    }

    private fun onCompilationClick(compilation: CompilationModel) {
        val audioTracks = toTrackWithChannels(compilation.tracks, compilation.channels)
        navigationActivity.updateRepo(
                compilation.tracks.first().id,
                MusicRepo(audioTracks.map(TrackWithChannel::toAudioTrack).toMutableList()),
                audioTracks
        )
    }
}