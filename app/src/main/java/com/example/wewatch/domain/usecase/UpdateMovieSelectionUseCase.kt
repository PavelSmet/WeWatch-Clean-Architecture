package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.repository.MovieRepository

class UpdateMovieSelectionUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(imdbId: String, selected: Boolean) {
        repository.updateMovieSelection(imdbId, selected)
    }
}
