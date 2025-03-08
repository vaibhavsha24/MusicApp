package com.example.musicplayer_vaibhav.util

import android.content.Context
import android.content.SharedPreferences
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.Song
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.models.SongResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefHelper {
    private const val PREF_NAME = "music_player_prefs"
    private const val KEY_SONGS = "songs_key"

    // Get SharedPreferences
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save songs to SharedPreferences
    fun saveSongs(context: Context, songs: SongResponse) {
        val sharedPref = getSharedPreferences(context)
        val gson = Gson()
        val json = gson.toJson(songs)
        sharedPref.edit().putString(KEY_SONGS, json).apply()
    }

    // Get songs from SharedPreferences
    fun getSongs(context: Context): SongResponse? {
        val sharedPref = getSharedPreferences(context)
        val gson = Gson()
        val json = sharedPref.getString(KEY_SONGS, null)
        return if (json != null) {
            val type = object : TypeToken<SongResponse>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }
}
