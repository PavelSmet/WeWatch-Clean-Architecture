package com.example.wewatch.domain.usecase

import com.example.wewatch.domain.repository.MovieRepository

class ClearSelectionsUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke() = repository.clearAllSelections()
}
