package com.example.wewatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMovieViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AddMovieContract.State())
    val state: StateFlow<AddMovieContract.State> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<AddMovieContract.Intent>()

    private val _effect = Channel<AddMovieContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: AddMovieContract.Intent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is AddMovieContract.Intent.MovieSelected -> {
                        _state.update { it.copy(movie = intent.movie, isButtonEnabled = true) }
                    }
                    is AddMovieContract.Intent.AddMovieClicked -> {
                        _state.value.movie?.let {
                            _effect.send(AddMovieContract.Effect.FinishWithResult(it))
                        } ?: run {
                            _effect.send(AddMovieContract.Effect.ShowToast("Сначала выберите фильм"))
                        }
                    }
                }
            }
        }
    }
}
