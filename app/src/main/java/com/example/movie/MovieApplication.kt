package com.example.movie

import android.app.Application
import android.util.Log
import com.bumptech.glide.Glide
import com.example.movie.repository.MovieRepository

class MovieApplication : Application() {
    lateinit var movieRepository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Glide
        try {
            Glide.get(this)
        } catch (e: Exception) {
            Log.e("MovieApplication", "Error initializing Glide: ${e.message}")
        }
        
        // Initialize repository
        movieRepository = MovieRepository()
    }

    companion object {
        private var instance: MovieApplication? = null

        fun getInstance(): MovieApplication {
            return instance ?: throw IllegalStateException("MovieApplication not initialized")
        }
    }

    init {
        instance = this
    }
}
