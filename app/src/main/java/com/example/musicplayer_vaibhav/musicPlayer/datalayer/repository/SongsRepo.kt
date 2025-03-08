package com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository

import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import com.example.musicplayer_vaibhav.networklayer.DataState
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun searchSongs(query: String, limit: Int): Flow<DataState<SongResponse>>
}

