package com.example.musicplayer_vaibhav.musicPlayer.datalayer.savanapiservice

import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApiService {
    @GET("api/search/songs")
    suspend fun searchSongs(@Query("query") query: String, @Query("limit") limit: Int): Response<SongResponse>
}
