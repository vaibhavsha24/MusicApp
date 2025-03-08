package com.example.musicplayer_vaibhav.networklayer

// Class to Wrap the response From the API based on Success Error
// Also added Loading State to show loading state in the UI.
sealed class DataState<out T> {
    // Success state use the T which is dynamic based on API called/
    data class Success<out T>(val baseResponseData: T) : DataState<T>()

    // Error state uses a error message which can be used to show on the UI and
    // for other purpose as well
    data class Error(
        val errorMessage: String? = null
    ) : DataState<Nothing>()

    object Loading : DataState<Nothing>()
}