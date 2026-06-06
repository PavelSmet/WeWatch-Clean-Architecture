package com.example.wewatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailsContract.State())
    val state: StateFlow<MovieDetailsContract.State> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<MovieDetailsContract.Intent>()

    private val _effect = Channel<MovieDetailsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: MovieDetailsContract.Intent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is MovieDetailsContract.Intent.Initialize -> {
                        _state.update { it.copy(movie = intent.movie) }
                    }
                    is MovieDetailsContract.Intent.PlayTrailer -> {
                        // Здесь в идеале должен быть запрос к YouTube API для поиска ID по названию
                        // Для примера используем тестовый ID (трейлер "Мстителей" или любой другой)
                        val demoVideoId = "TcMBFSGVi1c" 
                        _state.update { it.copy(isPlayerVisible = true, videoId = demoVideoId) }
                    }
                }
            }
        }
    }
}
