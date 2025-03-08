package com.example.musicplayer_vaibhav.networklayer

import com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository.SongRepository
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.repository.SongRepositoryImpl
import com.example.musicplayer_vaibhav.musicPlayer.datalayer.savanapiservice.SongsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// Dependency injection class to Provide the instances.
// USed Dagger Hilt for Dependency Injection
@InstallIn(SingletonComponent::class)
@Module
class DiProvider {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkClient(okHttpClient: OkHttpClient): NetworkClient {
        return NetworkClient(okHttpClient)
    }

    // API Service Provider with Base url from Network Provider class
    @Provides
    @Singleton
    fun provideApiService(
        @Named("baseUrl") baseUrl: String,
        networkClient: NetworkClient
    ): SongsApiService {
        return networkClient.getRetrofitInstance(baseUrl)
            .create(SongsApiService::class.java)
    }

    // API handler Provider.
    @Provides
    @Singleton
    fun provideApiHandler(): ApiHandler {
        return ApiHandler()
    }

    // Songs Repo provider to make api calls
    @Provides
    @Singleton
    fun provideSongsRepo(apiService: SongsApiService,apiHandler: ApiHandler): SongRepository {
        return SongRepositoryImpl(apiService,apiHandler)
    }
}