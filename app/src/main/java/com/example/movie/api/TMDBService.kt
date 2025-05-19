package com.example.movie.api

import com.example.movie.model.MovieResponse
import com.example.movie.model.Movie
import com.example.movie.model.MovieExportItem
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.gson.annotations.SerializedName

interface TMDBService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): Movie

    @GET("movie/daily/ids")
    suspend fun getDailyMovieIds(
        @Query("language") language: String = "en-US",
        @Query("date") date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    ): List<MovieExportItem>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: String = "22017678",
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "created_at.asc"
    ): MovieResponse

    @POST("account/{account_id}/favorite")
    suspend fun markAsFavorite(
        @Path("account_id") accountId: String = "22017678",
        @Body request: FavoriteRequest
    ): FavoriteResponse
}

data class FavoriteRequest(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("favorite") val favorite: Boolean
)

data class FavoriteResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String
)

object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjgwMTY1NWZkY2M2NDcyMmYxY2Y1NTQ2OTFhMmYwZiIsIm5iZiI6MTc0NzU0NjI1Mi42MDMwMDAyLCJzdWIiOiI2ODI5NzA4Yzg0NmFhMGE4NmNkYjI0NjkiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.wnZfjseZac4DcNMD6vY5vyHekuhyvf7jtzuUM6JB-k8"
    
    val instance: TMDBService by lazy {
        // Create auth interceptor to add token to requests
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $BEARER_TOKEN")
                .build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TMDBService::class.java)
    }
}
