package com.example.musicplayer_vaibhav.networklayer

import android.util.Log
import org.json.JSONObject
import com.google.gson.Gson
import retrofit2.Response

open class ApiHandler {


    // Function to make a network request using the provided service and method.
    suspend fun <T : Any> apiRequest(dataRequest: suspend () -> Response<T>): DataState<T> {
        return try {
            val response = dataRequest()
            if (response.isSuccessful) {
                response.body()?.let { DataState.Success(it) } ?: DataState.Error("Empty response body")
            } else {
                DataState.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            DataState.Error("Exception: ${e.message}")
        }
    }


}
