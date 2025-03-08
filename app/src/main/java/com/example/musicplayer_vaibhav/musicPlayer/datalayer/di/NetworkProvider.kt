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

    // Provides the Base Url to the API Service
    @Singleton
    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String {
       return  "https://saavn.dev/"
    }

}