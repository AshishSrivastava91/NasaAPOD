package com.goldman.nasa.ui.apod

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.goldman.nasa.R
import com.goldman.nasa.data.SessionManager
import com.goldman.nasa.data.model.Apod
import com.goldman.nasa.databinding.ScreenFirstBinding
import com.goldman.nasa.getAppComponent
import com.goldman.nasa.network.NetworkState.*
import com.goldman.nasa.util.TYPE_IMAGE
import io.reactivex.disposables.Disposable
import java.util.Calendar.*
import javax.inject.Inject


class NasaAPODScreen : Fragment(R.layout.screen_first) {

    @Inject lateinit var nasaAPODViewModelFactory: NasaAPODViewModelFactory
    @Inject lateinit var picasso: Picasso
    @Inject lateinit var  manager:SessionManager
    private lateinit var binding: ScreenFirstBinding
    private lateinit var videoLoadDisposable : Disposable
    private val nasaAPODViewModel: NasaAPODViewModel by navGraphViewModels(R.id.nav_main) { nasaAPODViewModelFactory }
    private lateinit var snackbar: Snackbar
    private lateinit var favSnackbar: Snackbar
    private lateinit var apodData:Apod
    private var animated : Boolean = false
    private var buttonActionId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        getAppComponent().injectApodScreen(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScreenFirstBinding.bind(view)
        insetWindow()
        init()
    }

    private fun init() {
        makeSnackBar()
        subscribeApodData()
        subscribeHdUrl()
        subscribeMediaType()
        binding.zoomPlay.setOnClickListener(buttonCLickListener)
        binding.buttonCalendar.setOnClickListener { showDatePicker() }
        binding.fav.setOnClickListener { showFav() }
        binding.buttonFav.setOnClickListener {
            manager.addToFavList(apodData)
            showSnackBarForFav() }
    }

    private fun showFav() {
        val intent = Intent(context, FavouritesActivity::class.java)
        startActivity(intent)    }

    private fun subscribeMediaType() {
        nasaAPODViewModel.mediaType.observe(this.viewLifecycleOwner, Observer {
            if (it != null) {
                when (it) {
                    TYPE_IMAGE -> {
                        buttonActionId = R.id.action_nasaAPODScreen_to_photoViewScreen
                    }
                }
            }
        })
    }

    private fun subscribeApodData() {
        nasaAPODViewModel.apodData.observe(this.viewLifecycleOwner, Observer {
            when (it) {
                is Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Success -> {
                    render(it.data)
                    apodData = it.data!!
                    hideSnackBar()
                    manager.saveDataInCache(apodData)
                }
                is NetworkError -> {
                    binding.progressBar.visibility = View.GONE
                    showDataFromCache()
                   // showSnackBar(getString(R.string.no_internet))
                }
                is Error -> {
                    binding.progressBar.visibility = View.GONE
                    showSnackBar(it.message)
                }
            }
        })
    }
    fun showDataFromCache(){
        apodData =  manager.getDataFromCache()
        render(apodData)
        hideSnackBar()

    }

    private fun subscribeHdUrl() {
        nasaAPODViewModel.hdUrl.observe(this.viewLifecycleOwner, Observer {
            binding.zoomPlay.isEnabled = it != null
        })
    }

    private fun makeSnackBar() {
        snackbar = Snackbar.make(
            binding.snackBarView,
            getString(R.string.no_internet),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.action_retry)) {
                nasaAPODViewModel.getApodForDate()
            }
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.nasa_white))
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.nasa_white))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackBar_color))


        favSnackbar = Snackbar.make(
            binding.snackBarView,
            getString(R.string.add_fav),
            Snackbar.LENGTH_SHORT
        )
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.nasa_white))
            .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.nasa_white))
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.snackBar_color))
    }

    private fun showSnackBar(text : String) {
        if (snackbar.isShownOrQueued) snackbar.dismiss()
        snackbar.setText(text)
        Handler().postDelayed({ snackbar.show() }, 500) //delay snackBar
    }
    private fun showSnackBarForFav() {
        if (favSnackbar.isShownOrQueued) favSnackbar.dismiss()
        favSnackbar.setText(getString(R.string.add_fav))
        Handler().postDelayed({ favSnackbar.show() }, 100)
    }

    private fun hideSnackBar() {
        if (snackbar.isShownOrQueued) snackbar.dismiss()
    }

    private fun render(apod: Apod?) {
        binding.zoomPlay.text = getString(R.string.view_image)
        binding.fav.text = getString(R.string.favourites)
        binding.tvHeadline.text = apod?.title
        binding.tvDescription.text = apod?.explanation
        loadImage(apod)
    }

    private fun loadImage(apod: Apod?) {
        if (!apod?.url.isNullOrEmpty()) {
            if (apod?.mediaType == TYPE_IMAGE) {
                picasso.load(apod.url)
                    .fit().centerCrop().into(binding.ivApod, imageLoadCallBack)
            }
        }
    }

    private val buttonCLickListener : View.OnClickListener = View.OnClickListener {
        animated = false
        findNavController().navigate(buttonActionId)
    }

    private val imageLoadCallBack: Callback = object : Callback {
        override fun onSuccess() {
            binding.progressBar.visibility = View.GONE
            if (!animated) animateViewsIn()
        }

        override fun onError(e: Exception?) {
            Log.e("Error loading APOD",e.toString())
            binding.progressBar.visibility = View.GONE
            showSnackBar(getString(R.string.error_loading_image))
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            //set date
            nasaAPODViewModel.setDate(
                year = year,
                month = month,
                day = dayOfMonth
            )
        }
        val datePicker = DatePickerDialog(
            requireContext(),
            R.style.Nasa_DatePickerTheme,
            dateSetListener,
            nasaAPODViewModel.getDate().get(YEAR),
            nasaAPODViewModel.getDate().get(MONTH),
            nasaAPODViewModel.getDate().get(DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = getInstance().timeInMillis
        datePicker.show()
    }

    /** Animation **/

    private fun animateViewsIn() {
        for (i in 0 until binding.container.childCount) {
            animateEachViewIn(binding.container.getChildAt(i), i)
        }
        animated = true
    }

    private fun animateEachViewIn(child: View, i: Int) {
        child.animate()
            .translationY(0f)
            .alpha(1f)
            .scaleX(1f).scaleY(1f)
            .setStartDelay(500).setDuration((200 * i).toLong())
            .setInterpolator(DecelerateInterpolator(2f))
            .start()
    }

    private fun insetWindow() {
        binding.root.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        binding.root.setOnApplyWindowInsetsListener { _, windowInsets ->

            val lpCalendar = binding.buttonCalendar.layoutParams as ViewGroup.MarginLayoutParams
            lpCalendar.apply {
                rightMargin += windowInsets.systemWindowInsetRight
                leftMargin += windowInsets.systemWindowInsetLeft
            }
            binding.buttonCalendar.layoutParams = lpCalendar

            val lpTvTitle = binding.tvHeadline.layoutParams as ViewGroup.MarginLayoutParams
            lpTvTitle.apply {
                topMargin += windowInsets.systemWindowInsetTop
                rightMargin += windowInsets.systemWindowInsetRight
                leftMargin += windowInsets.systemWindowInsetLeft
            }
            binding.tvHeadline.layoutParams = lpTvTitle

            val lpTvDescription = binding.scrollDescription.layoutParams as ViewGroup.MarginLayoutParams
            lpTvDescription.apply {
                rightMargin += windowInsets.systemWindowInsetRight
                leftMargin += windowInsets.systemWindowInsetLeft
            }
            binding.scrollDescription.layoutParams = lpTvDescription

            val lpCvZoomPlay = binding.zoomPlay.layoutParams as ViewGroup.MarginLayoutParams
            lpCvZoomPlay.apply {
                bottomMargin += windowInsets.systemWindowInsetBottom
                rightMargin += windowInsets.systemWindowInsetRight
                leftMargin += windowInsets.systemWindowInsetLeft
            }
            binding.zoomPlay.layoutParams = lpCvZoomPlay
            binding.root.setOnApplyWindowInsetsListener(null)
            return@setOnApplyWindowInsetsListener windowInsets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::videoLoadDisposable.isInitialized) videoLoadDisposable.dispose()
    }
}