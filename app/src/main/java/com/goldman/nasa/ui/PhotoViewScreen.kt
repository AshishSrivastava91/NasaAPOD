package com.goldman.nasa.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.goldman.nasa.R
import com.goldman.nasa.databinding.ScreenPhotoViewBinding
import com.goldman.nasa.getAppComponent
import com.goldman.nasa.ui.apod.NasaAPODViewModel
import com.goldman.nasa.ui.apod.NasaAPODViewModelFactory
import javax.inject.Inject


class PhotoViewScreen : Fragment(R.layout.screen_photo_view) {

    @Inject lateinit var nasaAPODViewModelFactory: NasaAPODViewModelFactory
    @Inject lateinit var picasso: Picasso
    private lateinit var binding: ScreenPhotoViewBinding
    private val nasaAPODViewModel: NasaAPODViewModel by navGraphViewModels(R.id.nav_main) { nasaAPODViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        getAppComponent().injectPhotoScreen(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScreenPhotoViewBinding.bind(view)
        insetWindow()
        nasaAPODViewModel.hdUrl.observe(this.viewLifecycleOwner, Observer {
            if (it != null) {
                picasso.load(it)
                    .resize(1080,1080)
                    .onlyScaleDown()
                    .into(binding.zoomView, imageLoadCallBack)
            }
        })
    }

    private val imageLoadCallBack: Callback = object : Callback {
        override fun onSuccess() {
            binding.progressBar.visibility = View.GONE
        }

        override fun onError(e: Exception?) {
            Log.e("Error loading APOD",e.toString())
            binding.progressBar.visibility = View.GONE
//            showSnackBar(getString(R.string.error_loading_image))
        }
    }

    private fun insetWindow() {
        binding.root.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }
}