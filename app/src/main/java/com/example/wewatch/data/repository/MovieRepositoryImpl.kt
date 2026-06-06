package com.example.wewatch.data.repository

import com.example.wewatch.api.OmdbApiService
import com.example.wewatch.data.MovieDao
import com.example.wewatch.data.mapper.toDomain
import com.example.wewatch.data.mapper.toEntity
import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val movieDao: MovieDao,
    private val apiService: OmdbApiService
) : MovieRepository {

    override fun getAllMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addMovie(movie: Movie) {
        movieDao.insertMovie(movie.toEntity())
    }

    override suspend fun addMovies(movies: List<Movie>) {
        movieDao.insertAllMovies(movies.map { it.toEntity() })
    }

    override suspend fun deleteMovies(movies: List<Movie>) {
        val movieIds = movies.map { it.imdbID }
        movieDao.deleteMoviesByIds(movieIds)
    }

    override suspend fun updateMovieSelection(imdbId: String, selected: Boolean) {
        movieDao.updateSelection(imdbId, selected)
    }

    override suspend fun clearAllSelections() {
        movieDao.clearAllSelections()
    }

    override suspend fun getSelectedMovies(): List<Movie> {
        return movieDao.getSelectedMovies().map { it.toDomain() }
    }

    override suspend fun isDatabaseEmpty(): Boolean {
        return movieDao.getMoviesSync().isEmpty()
    }

    override suspend fun searchMovies(query: String, year: String?): List<Movie> {
        return apiService.searchMovies(query, year)
    }

    override suspend fun getMovieDetails(imdbId: String): Movie {
        return apiService.getMovieDetails(imdbId)
    }
}
