package com.example.musicplayer_vaibhav.networklayer

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkProvider {

    @Singleton
    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String {
//       return // "https://saavn.dev/"
      return "https://run.mocky.io/"
    }

//fetched songs from this api but https://saavn.dev/ api is giving
// 500 internal server
// error mostly so developement and testing is almost impossible using this, so took the response from this api
// ad added a mock api using run.mocky.io and saved response there
}