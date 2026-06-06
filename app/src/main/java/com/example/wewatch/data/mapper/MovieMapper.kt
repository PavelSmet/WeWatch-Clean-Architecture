package com.example.wewatch.data.mapper

import com.example.wewatch.data.local.MovieEntity
import com.example.wewatch.domain.model.Movie

fun MovieEntity.toDomain(): Movie {
    return Movie(
        imdbID = imdbID,
        title = title,
        year = year,
        posterUrl = posterUrl,
        genre = genre,
        plot = plot,
        director = director,
        actors = actors,
        rating = rating,
        runtime = runtime,
        released = released,
        writer = writer,
        awards = awards,
        country = country,
        isSelected = isSelected
    )
}

fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        imdbID = imdbID,
        title = title,
        year = year,
        posterUrl = posterUrl,
        genre = genre,
        plot = plot,
        director = director,
        actors = actors,
        rating = rating,
        runtime = runtime,
        released = released,
        writer = writer,
        awards = awards,
        country = country,
        isSelected = isSelected
    )
}
