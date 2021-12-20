package com.goldman.nasa.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.goldman.nasa.data.model.Apod
import java.util.*

class SessionManager @SuppressLint("CommitPrefEdits")
constructor(context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor
    val apodFavList= ArrayList<Apod>()
    val gson = Gson()


    init {
        pref = context.getSharedPreferences(PREF_NAME, 0)
        editor = pref.edit()
    }

    var isFirstUse: Boolean
        get() = pref.getBoolean(IS_FIRST_USE, true)
        set(value) {
            pref.edit().putBoolean(IS_FIRST_USE,value).apply()
        }

    fun saveDataInCache(apodData: Apod){
        val json = gson.toJson(apodData)
        val editor = pref.edit()
        editor.putString("Set", json)
        editor.commit()
    }
    fun getDataFromCache():Apod{
        val json: String? = pref.getString("Set", "")
        var apod = gson.fromJson(json,Apod::class.java);
        return apod;
    }

    companion object {
        private const val PREF_NAME = "NasaAndroidPref"
        private const val IS_FIRST_USE = "IsFirstUse"
    }
    fun addToFavList(apodData: Apod){
            apodFavList.add(apodData)
    }
    fun getFavList():ArrayList<Apod>{
        return apodFavList;
    }
}