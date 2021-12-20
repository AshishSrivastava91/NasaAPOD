package com.goldman.nasa

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import com.goldman.nasa.di.DaggerMainComponent
import com.goldman.nasa.di.MainComponent


class Nasa : Application() {

    lateinit var appComponent: MainComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = getComponent()
    }

    private fun getComponent(): MainComponent {
        return DaggerMainComponent
            .builder()
            .context(this.applicationContext)
            .build()
    }

    companion object {
        fun getApp(context: Context): Nasa {
            return context.applicationContext as Nasa
        }
    }
}
fun Activity.getAppComponent(): MainComponent = Nasa.getApp(this).appComponent
fun Fragment.getAppComponent(): MainComponent = Nasa.getApp(this.requireContext()).appComponent