package com.example.musicplayer_vaibhav.musicPlayer.datalayer.savanapiservice

import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApiService {
    @GET("api/search/songs")
    suspend fun searchSongs(@Query("query") query: String, @Query("limit") limit: Int): Response<SongResponse>
//    @GET("v1/e4ba5eb1-e0cf-4de7-84df-f3cc7927c13f")
    @GET("v3/b3495c0a-8f93-42d4-b9c0-1df247928c7e")
    suspend fun searchSongsMockAPi():Response<SongResponse>
}
