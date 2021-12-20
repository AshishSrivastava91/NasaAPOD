package com.goldman.nasa.network


sealed class NetworkState<out T> {
    object Loading : NetworkState<Nothing>()
    object NetworkError : NetworkState<Nothing>()
    data class Error<T>(val message: String) : NetworkState<T>()
    data class Success<T>(val data: T?) : NetworkState<T>()
}