package com.example.wewatch.di

import com.example.wewatch.domain.repository.MovieRepository
import com.example.wewatch.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetMoviesUseCase(repository: MovieRepository) = GetMoviesUseCase(repository)

    @Provides
    @Singleton
    fun provideSearchMoviesUseCase(repository: MovieRepository) = SearchMoviesUseCase(repository)

    @Provides
    @Singleton
    fun provideAddMovieUseCase(repository: MovieRepository) = AddMovieUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteMoviesUseCase(repository: MovieRepository) = DeleteMoviesUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateMovieSelectionUseCase(repository: MovieRepository) = UpdateMovieSelectionUseCase(repository)

    @Provides
    @Singleton
    fun provideClearSelectionsUseCase(repository: MovieRepository) = ClearSelectionsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSelectedMoviesUseCase(repository: MovieRepository) = GetSelectedMoviesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMovieDetailsUseCase(repository: MovieRepository) = GetMovieDetailsUseCase(repository)
}
