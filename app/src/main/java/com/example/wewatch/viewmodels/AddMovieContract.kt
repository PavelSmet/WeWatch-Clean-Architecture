package com.example.wewatch.viewmodels

import com.example.wewatch.domain.model.Movie

class AddMovieContract {
    // Состояние экрана добавления
    data class State(
        val movie: Movie? = null,
        val isButtonEnabled: Boolean = false
    )

    // Намерения
    sealed class Intent {
        data class MovieSelected(val movie: Movie) : Intent()
        object AddMovieClicked : Intent()
    }

    // Эффекты
    sealed class Effect {
        data class FinishWithResult(val movie: Movie) : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}
