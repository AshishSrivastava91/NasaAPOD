package com.goldman.nasa.ui.apod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NasaAPODViewModelFactory @Inject constructor(
    private val nasaAPODRepository: NasaAPODRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NasaAPODViewModel(
            nasaAPODRepository
        ) as T
    }
}