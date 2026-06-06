package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository

class AddMovieUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movie: Movie) = repository.addMovie(movie)
}
