package com.example.movie.api

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.example.movie.model.MovieExportItem

class TMDBExportService {
    private val client = OkHttpClient.Builder()
        .build()
    
    private val gson = Gson()
    private val baseUrl = "http://files.tmdb.org/p/exports"
    
    suspend fun fetchMovieIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        val formattedDate = date.format(DateTimeFormatter.ofPattern("MM_dd_yyyy"))
        val url = "$baseUrl/movie_ids_$formattedDate.json.gz"
        
        val request = Request.Builder()
            .url(url)
            .build()
            
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch movie IDs: ${response.code}")
        }
        
        val movieIds = mutableListOf<MovieExportItem>()
        
        response.body?.byteStream()?.let { inputStream ->
            GZIPInputStream(inputStream).use { gzipStream ->
                BufferedReader(InputStreamReader(gzipStream)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        try {
                            val item = gson.fromJson(line, MovieExportItem::class.java)
                            movieIds.add(item)
                        } catch (e: Exception) {
                            // Skip invalid JSON lines
                        }
                    }
                }
            }
        }
        
        movieIds
    }

    suspend fun fetchTvSeriesIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        val formattedDate = date.format(DateTimeFormatter.ofPattern("MM_dd_yyyy"))
        val url = "$baseUrl/tv_series_ids_$formattedDate.json.gz"
        // Similar implementation as fetchMovieIds
        emptyList() // TODO: Implement if needed
    }

    suspend fun fetchPersonIds(date: LocalDate = LocalDate.now()): List<MovieExportItem> = withContext(Dispatchers.IO) {
        val formattedDate = date.format(DateTimeFormatter.ofPattern("MM_dd_yyyy"))
        val url = "$baseUrl/person_ids_$formattedDate.json.gz"
        // Similar implementation as fetchMovieIds
        emptyList() // TODO: Implement if needed
    }
}
