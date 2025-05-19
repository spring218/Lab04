package com.example.movie.repository

import com.example.movie.api.RetrofitClient
import com.example.movie.api.TMDBExportService
import com.example.movie.api.FavoriteRequest
import com.example.movie.model.Movie
import com.example.movie.model.MovieExportItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieRepository {
    private val api = RetrofitClient.instance
    private val exportService = TMDBExportService()

    suspend fun getPopularMovies(): List<Movie> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPopularMovies()
            response.results
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyMovieIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        try {
            exportService.fetchMovieIds(date)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyTvSeriesIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        try {
            exportService.fetchTvSeriesIds(date)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getDailyPersonIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        try {
            exportService.fetchPersonIds(date)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getFavoriteMovies(): List<Movie> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavoriteMovies()
            response.results
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun markAsFavorite(movieId: Int, favorite: Boolean) = withContext(Dispatchers.IO) {
        try {
            api.markAsFavorite(request = FavoriteRequest(mediaId = movieId, favorite = favorite))
        } catch (e: Exception) {
            throw e
        }
    }
}
