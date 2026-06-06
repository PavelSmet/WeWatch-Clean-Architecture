package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Flow<List<Movie>> = repository.getAllMovies()
}
