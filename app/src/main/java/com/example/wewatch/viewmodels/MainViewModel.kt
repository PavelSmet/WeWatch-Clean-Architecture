package com.example.wewatch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatch.domain.model.Movie
import com.example.wewatch.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    private val deleteMoviesUseCase: DeleteMoviesUseCase,
    private val updateMovieSelectionUseCase: UpdateMovieSelectionUseCase,
    private val clearSelectionsUseCase: ClearSelectionsUseCase,
    private val getSelectedMoviesUseCase: GetSelectedMoviesUseCase,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainContract.State())
    val state: StateFlow<MainContract.State> = _state.asStateFlow()

    private val _effect = Channel<MainContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadMovies()
    }

    fun sendIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.LoadMovies -> loadMovies()
            is MainContract.Intent.AddMovie -> addMovie(intent.movie)
            is MainContract.Intent.UpdateMovieSelection -> updateMovieSelection(intent.movie, intent.isSelected)
            is MainContract.Intent.DeleteSelectedMovies -> deleteSelectedMovies()
            is MainContract.Intent.ClearSelection -> clearSelection()
            is MainContract.Intent.MovieClicked -> {
                viewModelScope.launch {
                    _effect.send(MainContract.Effect.NavigateToDetails(intent.movie))
                }
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMoviesUseCase().collect { movieList ->
                val selectedCount = movieList.count { it.isSelected }
                _state.update { 
                    it.copy(
                        movies = movieList, 
                        isLoading = false,
                        isSelectionMode = selectedCount > 0,
                        selectedCount = selectedCount
                    ) 
                }
            }
        }
    }

    private fun addMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fullMovie = getMovieDetailsUseCase(movie.imdbID)
                addMovieUseCase(fullMovie)
            } catch (e: Exception) {
                addMovieUseCase(movie)
            }
        }
    }

    private fun deleteSelectedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedMovies = getSelectedMoviesUseCase()
            if (selectedMovies.isNotEmpty()) {
                deleteMoviesUseCase(selectedMovies)
                clearSelectionsUseCase()
            }
        }
    }

    private fun updateMovieSelection(movie: Movie, isSelected: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            updateMovieSelectionUseCase(movie.imdbID, isSelected)
        }
    }

    private fun clearSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            clearSelectionsUseCase()
        }
    }
}
