package com.example.wewatch.di

import com.example.wewatch.api.OmdbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOmdbApiService(): OmdbApiService {
        return OmdbApiService()
    }
}
