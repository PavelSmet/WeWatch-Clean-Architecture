package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository

class GetSelectedMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(): List<Movie> = repository.getSelectedMovies()
}
