package com.example.wewatch.di

import com.example.wewatch.api.OmdbApiService
import com.example.wewatch.data.MovieDao
import com.example.wewatch.data.repository.MovieRepositoryImpl
import com.example.wewatch.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieDao: MovieDao,
        apiService: OmdbApiService
    ): MovieRepository {
        return MovieRepositoryImpl(movieDao, apiService)
    }
}
