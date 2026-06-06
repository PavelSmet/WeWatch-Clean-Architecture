package com.example.wewatch.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
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
    var isSelected: Boolean = false
) : Parcelable
