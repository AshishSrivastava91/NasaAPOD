package com.goldman.nasa.network

import com.goldman.nasa.data.model.Apod
import com.goldman.nasa.util.APOD_ENDPOINT
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface NasaApiInterface {

    @GET(APOD_ENDPOINT)
    fun getDefaultApod(
        @Query("api_key") apiKey: String
    ): Single<Apod>

    @GET(APOD_ENDPOINT)
    fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Single<Apod>

}