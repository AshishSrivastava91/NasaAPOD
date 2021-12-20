package com.goldman.nasa.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.goldman.nasa.R
import com.goldman.nasa.data.model.Apod
import com.goldman.nasa.util.TYPE_IMAGE

class FavouriteListAdapter(private var moviesList: List<Apod>,private var picasso: Picasso) :
    RecyclerView.Adapter<FavouriteListAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var pofDay:ImageView = view.findViewById(R.id.iv_apod)
        var expl:TextView    = view.findViewById(R.id.title)
    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_list, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movie = moviesList[position]
        holder.expl.text = movie.explanation
        loadImage(movie,holder.pofDay)
    }

    private fun loadImage(apod: Apod?,iv:ImageView) {
        if (!apod?.url.isNullOrEmpty()) {
            if (apod?.mediaType == TYPE_IMAGE) {
                picasso.load(apod.url)
                    .fit().centerCrop().into(iv)
            }
        }
    }
    override fun getItemCount(): Int {
        return moviesList.size
    }
}