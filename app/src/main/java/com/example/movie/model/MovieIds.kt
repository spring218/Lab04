package com.example.movie.model

import com.google.gson.annotations.SerializedName

data class MovieIdsResponse(
    @SerializedName("ids") val ids: List<Int>
)
