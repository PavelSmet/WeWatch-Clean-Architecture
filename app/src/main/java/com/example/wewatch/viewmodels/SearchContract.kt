package com.example.wewatch.viewmodels

import com.example.wewatch.domain.model.Movie

class SearchContract {
    // Состояние экрана поиска
    data class State(
        val searchResults: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    // Намерения пользователя
    sealed class Intent {
        data class SearchMovies(val query: String, val year: String? = null) : Intent()
    }

    // Побочные эффекты
    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }
}
