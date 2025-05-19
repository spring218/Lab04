package com.example.movie.model

import com.google.gson.annotations.SerializedName

data class MovieExportItem(
    val id: Int,
    @SerializedName("original_title") val originalTitle: String?,
    val popularity: Double,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("video") val isVideo: Boolean
)
