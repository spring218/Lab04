package com.example.movie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.core.content.ContextCompat
import com.example.movie.api.RetrofitClient
import com.example.movie.api.TMDBExportService
import com.example.movie.model.Movie
import com.example.movie.ui.CardPresenter
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainFragment : BrowseSupportFragment() {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private val exportService = TMDBExportService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            adapter = rowsAdapter
            
            setupUIElements()
            setupEventListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error in setup: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            loadMovies()
            loadDailyMovieIds()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupUIElements() {
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.purple_500)
    }    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Movie -> {
                    val intent = Intent(activity, MovieDetailsActivity::class.java)
                    intent.putExtra("movie", item)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadMovies() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getPopularMovies()
                val movies = response.results

                if (movies.isEmpty()) {
                    Toast.makeText(context, "No movies found", Toast.LENGTH_LONG).show()
                    return@launch
                }
                
                val listRowAdapter = ArrayObjectAdapter(CardPresenter())
                movies.forEach { movie ->
                    listRowAdapter.add(movie)
                }

                val header = HeaderItem(0, "Popular Movies")
                rowsAdapter.add(ListRow(header, listRowAdapter))
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("401") == true -> "Authentication failed. Please check the bearer token."
                    e.message?.contains("404") == true -> "API endpoint not found"
                    else -> "Error loading movies: ${e.message}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadDailyMovieIds() {
        lifecycleScope.launch {
            try {
                val date = LocalDate.of(2025, 5, 18)
                val movieIds = exportService.fetchMovieIds(date)
                Toast.makeText(context, "Found ${movieIds.size} movie IDs", Toast.LENGTH_SHORT).show()
                
                // Create a new row for some of these movies (first 20)
                val exportRowAdapter = ArrayObjectAdapter(CardPresenter())
                val idsToFetch = movieIds.take(20)
                
                for (movieExport in idsToFetch) {
                    try {
                        val movie = RetrofitClient.instance.getMovie(movieExport.id)
                        exportRowAdapter.add(movie)
                    } catch (e: Exception) {
                        // Skip failed movies
                        continue
                    }
                }

                if (exportRowAdapter.size() > 0) {
                    val header = HeaderItem(1, "Daily Movies")
                    rowsAdapter.add(ListRow(header, exportRowAdapter))
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading daily IDs: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
