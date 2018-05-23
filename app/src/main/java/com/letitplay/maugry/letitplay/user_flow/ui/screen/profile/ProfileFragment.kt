package com.letitplay.maugry.letitplay.user_flow.ui.screen.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.gsfoxpro.musicservice.MusicRepo
import com.letitplay.maugry.letitplay.R
import com.letitplay.maugry.letitplay.ServiceLocator
import com.letitplay.maugry.letitplay.ServiceLocator.router
import com.letitplay.maugry.letitplay.data_management.db.entity.Language
import com.letitplay.maugry.letitplay.data_management.db.entity.Track
import com.letitplay.maugry.letitplay.data_management.db.entity.TrackWithChannel
import com.letitplay.maugry.letitplay.data_management.model.toAudioTrack
import com.letitplay.maugry.letitplay.user_flow.ui.BaseFragment
import com.letitplay.maugry.letitplay.user_flow.ui.ChannelListType
import com.letitplay.maugry.letitplay.user_flow.ui.screen.channels_and_catalog.channels.ChannelKey
import com.letitplay.maugry.letitplay.user_flow.ui.screen.search.query.SearchResultsKey
import com.letitplay.maugry.letitplay.user_flow.ui.utils.DateHelper
import com.letitplay.maugry.letitplay.user_flow.ui.utils.listDivider
import com.letitplay.maugry.letitplay.utils.Optional
import com.letitplay.maugry.letitplay.utils.PreferenceHelper
import com.letitplay.maugry.letitplay.utils.PreferenceHelper.Companion.PROFILE_FILENAME
import com.letitplay.maugry.letitplay.utils.ext.getUriForFile
import com.letitplay.maugry.letitplay.utils.ext.getUriForImageFile
import com.letitplay.maugry.letitplay.utils.ext.imageFile
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.view_language_dialog.view.*
import timber.log.Timber
import java.util.*


class ProfileFragment : BaseFragment(R.layout.profile_fragment) {
    private val REQUEST_IMAGE_CAPTURE = 1

    private val likedTracksListAdapter: LikedTracksAdapter by lazy {
        LikedTracksAdapter(musicService, ::playTrack, ::onOtherClick)
    }

    private val vm by lazy {
        ViewModelProvider(this, ServiceLocator.viewModelFactory)
                .get(ProfileViewModel::class.java)
    }

    private var profileRepo: MusicRepo? = null
    lateinit var prefHelper: PreferenceHelper

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm.isReported.observe(this, Observer<Boolean> {
            it?.let {
                if (it) Toast.makeText(ctx, R.string.report_message, Toast.LENGTH_LONG).show()
            }
        })
        vm.likedTracks.observe(this, Observer<List<TrackWithChannel>> {

            profile_track_count.text = it?.count()?.toString() ?: "0"
            profile_tracks_time.text = DateHelper.getTime(it?.sumBy { it.track.totalLengthInSeconds }
                    ?: 0)

            it?.let {
                likedTracksListAdapter.data = it
            }
        })
        vm.isAdult.observe(this, Observer<Boolean> {
            it?.let {
                prefHelper.isAdult = it
                profile_adult_content.isChecked = it
            }
        })
        vm.language.observe(this, Observer<Optional<Language>> {
            val lang = it?.value
            lang?.let {
                profile_current_language.text = getString(when (it) {
                    Language.RU -> R.string.language_ru
                    Language.EN -> R.string.language_en
                    Language.FR -> R.string.language_fr
                    Language.ZH -> R.string.language_zh
                })
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        context?.let {
            prefHelper = it.let { PreferenceHelper(it) }
            if (prefHelper.userName != PreferenceHelper.DEFAULT_USER_NAME)
                profile_user_name.setText(prefHelper.userName, TextView.BufferType.EDITABLE)
            else profile_user_name.setText(getString(R.string.profile_user_name), TextView.BufferType.EDITABLE)

            profile_user_name.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {
                    prefHelper.userName = p0.toString()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
            })
            vm.isAdult.value = prefHelper.isAdult
        }
        profile_adult_content.setOnClickListener {
            AlertDialog.Builder(activity)
                    .apply {
                        setMessage(R.string.profile_adult_message)
                        setPositiveButton(R.string.profile_yes, object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                vm.putIsAdult()
                            }
                        })
                        setNegativeButton(R.string.profile_no, object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                vm.deleteIsAdult()
                            }
                        })
                    }.create().show()
        }

        profile_hidden_channels_card.setOnClickListener {
            onHideChannelClick()
        }
        profile_header.attachTo(profile_list)
        loadProfileAvatar()
        profile_photo_icon.setOnClickListener(::takePhoto)
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
                dialogView.zh_button.setOnClickListener(onLanguageClick)
                setContentView(dialogView)
                show()
            }
        }
    }

    private fun onHideChannelClick() {
        router.navigateTo(ChannelKey(ChannelListType.BLACKLIST))
    }

    private fun takePhoto(view: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile = context?.imageFile(PROFILE_FILENAME)
            if (photoFile != null) {
                val photoURI = requireContext().getUriForFile(photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun loadProfileAvatar() {
        val uri = requireContext().getUriForImageFile(PROFILE_FILENAME)
        Glide.with(profile_user_avatar_pic)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions().signature(ObjectKey(UUID.randomUUID().toString()))
                        .placeholder(R.drawable.profile_placeholder))
                .into(profile_user_avatar_pic)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            loadProfileAvatar()
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
            navigationActivity.updateRepo(track.id, profileRepo, it)
        }
    }

    private fun onOtherClick(trackId: Int, reason: Int) {
        vm.onReportClick(trackId, reason)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            Timber.d("Navigate to results page")
            navigationActivity.navigateTo(SearchResultsKey())
        }
        return super.onOptionsItemSelected(item)
    }
}