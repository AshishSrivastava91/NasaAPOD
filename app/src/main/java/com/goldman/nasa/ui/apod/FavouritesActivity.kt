package com.goldman.nasa.ui.apod

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.goldman.nasa.R
import com.goldman.nasa.data.SessionManager
import com.goldman.nasa.data.model.Apod
import com.goldman.nasa.getAppComponent
import com.goldman.nasa.ui.FavouriteListAdapter
import javax.inject.Inject

class FavouritesActivity : AppCompatActivity() {
    private var movieList = ArrayList<Apod>()
    @Inject
    lateinit var nasaAPODViewModelFactory: NasaAPODViewModelFactory
    @Inject lateinit var manager:SessionManager
    @Inject lateinit var picasso: Picasso
    private lateinit var moviesAdapter: FavouriteListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        getAppComponent().injectFavouritesActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        moviesAdapter = FavouriteListAdapter(movieList,picasso)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = moviesAdapter
        prepareMovieData()
    }
    private fun prepareMovieData() {
        movieList.addAll(manager.getFavList())
        moviesAdapter.notifyDataSetChanged()
    }
}