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

    @Provides
    @Singleton
    fun provideApiService(
        @Named("baseUrl") baseUrl: String,
        networkClient: NetworkClient
    ): SongsApiService {
        return networkClient.getRetrofitInstance(baseUrl)
            .create(SongsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiHandler(): ApiHandler {
        return ApiHandler()
    }

    @Provides
    @Singleton
    fun provideSongsRepo(apiService: SongsApiService,apiHandler: ApiHandler): SongRepository {
        return SongRepositoryImpl(apiService,apiHandler)
    }
}