package com.example.musicplayer_vaibhav.musicPlayer.viewmodel

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.Song
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository.SongRepository
import com.example.musicplayer_vaibhav.networklayer.DataState
import com.example.musicplayer_vaibhav.util.SharedPrefHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
// used DI to Provide Repo Instance
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

    // Fetch Songs from API.
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
                            // Save the Response to Local Storage
                            SharedPrefHelper.saveSongs(context,songsResponse)
                            _songsResponse.postValue(it)
                        }

                        is DataState.Error -> {
                            // In case the API fails use the already stored Response from Local Storage
                             val localSongs = SharedPrefHelper.getSongs(context)
                            if (localSongs!=null) {
                                localSongs?.data?.results?.let { songs ->
                                    _songs.value = songs
                                    _currentSong.value = songs?.getOrNull(0)
                                }
                                _songsResponse.postValue(DataState.Success(localSongs))
                            } else {
                                // else post error.
                                _songsResponse.postValue(it)
                            }
                        } else ->{}
                    }
                }.collect()
            }
    }

    // Change value on the basis of Play pause clicked
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    // shuffle the songs array
    fun shuffleSongs() {
        _songs.value = _songs.value.shuffled()
        _currentSong.value = _songs.value[_currentSongIndex.value]

    }

    // used to play initially music
    fun playMusic(context: Context) {
        _isPlaying.value = true
    }

    // Next Click handled and current song and index updated
    fun nextClicked() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex < _songs.value.size - 1) {
            _currentSong.value = _songs.value[currentIndex + 1]
            _currentSongIndex.value = currentIndex + 1
        }
    }

    // Previous Click Handled and current song and index updated
    fun previousClicked() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex > 0) {
            _currentSong.value = _songs.value[currentIndex - 1]
            _currentSongIndex.value = currentIndex - 1
        }
    }

    // use of scroll listener to select the song
    fun selectSong(index: Int) {
        _currentSongIndex.value = index
        _currentSong.value = _songs.value[index]
    }

    // PIP actions handled
    @OptIn(UnstableApi::class)
    fun handlePiPAction(action: String) {
        when (action) {
            PlayerNotificationManager.ACTION_PLAY -> {
                togglePlayPause()
            }
            PlayerNotificationManager.ACTION_PREVIOUS -> {
                previousClicked()
            }
            PlayerNotificationManager.ACTION_NEXT -> {
                nextClicked()
            }
        }
    }

    // In case of error fetch local songs response saved for testing purpose
    fun fetchLocal(context: Context){
        try {
            // Open the local JSON file from assets
            val inputStream = context.assets.open("music_response.json")
            val reader = InputStreamReader(inputStream)


            // Use Gson to parse the JSON data into the object
            val gson = Gson()
            var response = gson.fromJson<Any>(reader, SongResponse::class.java) as SongResponse

            response?.data?.results?.let { songs ->
                _songs.value = songs
                _currentSong.value = songs?.getOrNull(0)
            }
            SharedPrefHelper.saveSongs(context,response)
            _songsResponse.postValue(DataState.Success(response))

        } catch (e: Exception) {
            _songsResponse.postValue(DataState.Error("Something Went Wrong"))
        }
    }
}

