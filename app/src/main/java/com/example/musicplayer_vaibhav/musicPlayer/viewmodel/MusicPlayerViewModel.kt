package com.example.musicplayer_vaibhav.musicPlayer.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.Song
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository.SongRepository
import com.example.musicplayer_vaibhav.networklayer.DataState
import com.example.musicplayer_vaibhav.util.SharedPrefHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(private val songsRepo: SongRepository) :
    ViewModel() {

    private val _songsResponse = MutableLiveData<DataState<SongResponse>>()
    val songsResponse: LiveData<DataState<SongResponse>> = _songsResponse

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _currentSongIndex = MutableStateFlow<Int>(0)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    fun fetchSongs(context:Context) {
            viewModelScope.launch {
                songsRepo.searchSongs("HINDI TOP", 100).onEach { it ->
                    when (it) {
                        is DataState.Success -> {
                            var songsResponse = it.baseResponseData
                            songsResponse?.data?.results?.let { songs ->
                                _songs.value = songs
                                _currentSong.value = songs?.getOrNull(0)
                            }
                            SharedPrefHelper.saveSongs(context,songsResponse)
                            _songsResponse.postValue(it)
                        }

                        is DataState.Error -> {
                             val localSongs = SharedPrefHelper.getSongs(context)
                            if (localSongs!=null) {
                                localSongs?.data?.results?.let { songs ->
                                    _songs.value = songs
                                    _currentSong.value = songs?.getOrNull(0)
                                }
                                _songsResponse.postValue(DataState.Success(localSongs))
                            } else {
                                _songsResponse.postValue(it)
                            }
                        } else ->{}
                    }
                }.collect()
            }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun shuffleSongs() {
        _songs.value = _songs.value.shuffled()
        _currentSong.value = _songs.value[_currentSongIndex.value]

    }

    fun playMusic(context: Context) {
        _isPlaying.value = true
    }

    fun nextClicked() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex < _songs.value.size - 1) {
            _currentSong.value = _songs.value[currentIndex + 1]
            _currentSongIndex.value = currentIndex + 1
        }
    }

    fun previousClicked() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex > 0) {
            _currentSong.value = _songs.value[currentIndex - 1]
            _currentSongIndex.value = currentIndex - 1
        }
    }

    fun selectSong(index: Int) {
        _currentSongIndex.value = index
        _currentSong.value = _songs.value[index]
    }
}

