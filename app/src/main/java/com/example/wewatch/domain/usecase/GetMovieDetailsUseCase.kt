package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.repository.MovieRepository

class GetMovieDetailsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(imdbId: String): Movie = repository.getMovieDetails(imdbId)
}
