package com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository

import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.savanapiservice.SongsApiService
import com.example.musicplayer_vaibhav.networklayer.ApiHandler
import com.example.musicplayer_vaibhav.networklayer.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SongRepositoryImpl(
    private val apiService: SongsApiService,
    private val apiHandler: ApiHandler
) : SongRepository {

    override fun searchSongs(query: String, limit: Int): Flow<DataState<SongResponse>> = flow {
        emit(DataState.Loading)
        emit( apiHandler?.apiRequest { apiService.searchSongs(query, limit) })

    }.flowOn(Dispatchers.IO) as Flow<DataState<SongResponse>>

}