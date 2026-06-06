package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository

class DeleteMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movies: List<Movie>) = repository.deleteMovies(movies)
}
