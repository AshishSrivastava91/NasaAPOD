package com.goldman.nasa.ui.apod

import com.goldman.nasa.data.model.Apod
import com.goldman.nasa.network.NasaApiInterface
import com.goldman.nasa.util.API_KEY
import io.reactivex.Single
import javax.inject.Inject

class NasaAPODRepository @Inject constructor(
    private val nasaApiInterface: NasaApiInterface
) {

    fun getApod(date : String?) : Single<Apod> = nasaApiInterface.getApod(API_KEY,date ?: "2020-12-19")

    fun getDefaultApod() : Single<Apod> = nasaApiInterface.getDefaultApod(API_KEY)
}