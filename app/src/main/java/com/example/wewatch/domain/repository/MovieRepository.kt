package com.example.wewatch.domain.repository

import com.example.wewatch.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    // Local data
    fun getAllMovies(): Flow<List<Movie>>
    suspend fun addMovie(movie: Movie)
    suspend fun addMovies(movies: List<Movie>)
    suspend fun deleteMovies(movies: List<Movie>)
    suspend fun updateMovieSelection(imdbId: String, selected: Boolean)
    suspend fun clearAllSelections()
    suspend fun getSelectedMovies(): List<Movie>
    suspend fun isDatabaseEmpty(): Boolean

    // Remote data
    suspend fun searchMovies(query: String, year: String? = null): List<Movie>
    suspend fun getMovieDetails(imdbId: String): Movie
}
