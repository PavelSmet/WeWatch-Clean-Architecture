package com.example.wewatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatch.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchContract.State())
    val state: StateFlow<SearchContract.State> = _state.asStateFlow()

    private val _intent = MutableSharedFlow<SearchContract.Intent>()

    private val _effect = Channel<SearchContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        handleIntents()
    }

    fun sendIntent(intent: SearchContract.Intent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                when (intent) {
                    is SearchContract.Intent.SearchMovies -> searchMovies(intent.query, intent.year)
                }
            }
        }
    }

    private fun searchMovies(query: String, year: String?) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val results = searchMoviesUseCase(query, year)
                _state.update { 
                    it.copy(
                        searchResults = results, 
                        isLoading = false,
                        error = if (results.isEmpty()) "Ничего не найдено" else null
                    ) 
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        searchResults = emptyList(),
                        isLoading = false,
                        error = "Ошибка поиска: ${e.message}"
                    ) 
                }
                _effect.send(SearchContract.Effect.ShowToast("Ошибка при поиске"))
            }
        }
    }
}
