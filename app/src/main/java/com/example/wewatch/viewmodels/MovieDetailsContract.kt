package com.example.wewatch.viewmodels

import com.example.wewatch.domain.model.Movie

class MovieDetailsContract {
    data class State(
        val movie: Movie? = null,
        val isPlayerVisible: Boolean = false,
        val videoId: String? = null
    )

    sealed class Intent {
        data class Initialize(val movie: Movie) : Intent()
        object PlayTrailer : Intent()
    }

    sealed class Effect {
        data class ShowError(val message: String) : Effect()
    }
}
