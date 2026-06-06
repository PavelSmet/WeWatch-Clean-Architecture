package com.example.wewatch.data

import androidx.room.*
import com.example.wewatch.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieEntity>>  // Реактивный поток всех фильмов

    @Query("SELECT * FROM movies")
    suspend fun getMoviesSync(): List<MovieEntity>  // Для проверок

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovies(movies: List<MovieEntity>)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)

    @Query("DELETE FROM movies WHERE imdbID IN (:movieIds)")
    suspend fun deleteMoviesByIds(movieIds: List<String>)

    @Query("UPDATE movies SET isSelected = :selected WHERE imdbID = :imdbId")
    suspend fun updateSelection(imdbId: String, selected: Boolean)

    @Query("UPDATE movies SET isSelected = 0")
    suspend fun clearAllSelections()  // Сброс всех чекбоксов

    @Query("SELECT * FROM movies WHERE isSelected = 1")
    suspend fun getSelectedMovies(): List<MovieEntity>  // Выбранные для удаления
}