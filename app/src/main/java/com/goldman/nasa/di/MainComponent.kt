package com.goldman.nasa.di

import android.content.Context
import com.goldman.nasa.di.module.ApplicationModule
import com.goldman.nasa.di.module.NetworkModule
import com.goldman.nasa.di.module.PicassoModule
import com.goldman.nasa.di.module.RetrofitModule
import com.goldman.nasa.ui.PhotoViewScreen
import com.goldman.nasa.ui.VideoViewScreen
import com.goldman.nasa.ui.apod.FavouritesActivity
import com.goldman.nasa.ui.apod.NasaAPODScreen
import dagger.BindsInstance
import dagger.Component


@AppScope
@Component(
    modules = [ApplicationModule::class, NetworkModule::class, PicassoModule::class, RetrofitModule::class]
)
interface MainComponent {
    fun injectApodScreen(nasaAPODScreen: NasaAPODScreen)
    fun injectPhotoScreen(photoViewScreen: PhotoViewScreen)
    fun injectVideoScreen(videoViewScreen: VideoViewScreen)
    fun injectFavouritesActivity(favrouite:FavouritesActivity)

    @Component.Builder
    interface Builder {
        fun build() : MainComponent
        @BindsInstance fun context(@ApplicationContext context : Context) : Builder
    }
}