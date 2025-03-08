package com.example.musicplayer_vaibhav.networklayer


sealed class DataState<out T> {
    data class Success<out T>(val baseResponseData: T) : DataState<T>()
    data class Error(
        val errorMessage: String? = null
    ): DataState<Nothing>()
    object Loading : DataState<Nothing>()
}