package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String, year: String? = null): List<Movie> {
        return repository.searchMovies(query, year)
    }
}
