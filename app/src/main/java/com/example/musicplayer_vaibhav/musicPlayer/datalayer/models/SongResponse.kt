package com.example.musicplayer_vaibhav.musicPlayer.datalayer.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongResponse(
    var data:SongData
) : Parcelable

@Parcelize
data class SongData(
    var results:ArrayList<Song>
) : Parcelable

@Parcelize
data class Song(
    val id: String?,
    val name: String?,
    val type: String?,
    val year: String?,
    val releaseDate: String?,
    val duration: Int?,
    val label: String?,
    val explicitContent: Boolean?,
    val playCount: Int?,
    val language: String?,
    val hasLyrics: Boolean?,
    val lyricsId: String?,
    val url: String?,
    val copyright: String?,
    val album: Album?,
    val artists: Artists?,
    val featured: List<Artist>?,
    val all: List<Artist>?,
    val image: List<Image>?,
    val downloadUrl: List<DownloadUrl>?
):Parcelable

@Parcelize
data class Album(
    val id: String?,
    val name: String?,
    val url: String?
) : Parcelable

@Parcelize
data class Artists(
    val primary: List<Artist>?
) : Parcelable

@Parcelize
data class Artist(
    val id: String?,
    val name: String?,
    val role: String?,
    val image: List<Image>?,
    val type: String?,
    val url: String?
) : Parcelable

@Parcelize
data class Image(
    val quality: String?,
    val url: String?
) : Parcelable

@Parcelize
data class DownloadUrl(
    val quality: String?,
    val url: String?
) : Parcelable
