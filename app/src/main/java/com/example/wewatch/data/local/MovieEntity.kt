package com.example.wewatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val imdbID: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    val genre: String? = null,
    val plot: String? = null,
    val director: String? = null,
    val actors: String? = null,
    val rating: String? = null,
    val runtime: String? = null,
    val released: String? = null,
    val writer: String? = null,
    val awards: String? = null,
    val country: String? = null,
    val isSelected: Boolean = false
)
