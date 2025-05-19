package com.example.movie.ui

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.example.movie.R
import com.example.movie.model.Movie

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            val width = parent.resources.getDimensionPixelSize(R.dimen.card_width)
            val height = parent.resources.getDimensionPixelSize(R.dimen.card_height)
            setMainImageDimensions(width, height)
        }
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val movie = item as Movie
        val cardView = viewHolder.view as ImageCardView
        
        cardView.titleText = movie.title
        cardView.contentText = movie.overview
        val posterUrl = if (movie.posterPath.isNullOrEmpty()) {
            null
        } else {
            "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        }
        
        Glide.with(cardView.context)
            .load(posterUrl)
            .centerCrop()
            .error(R.drawable.default_background)
            .placeholder(R.drawable.default_background)
            .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.mainImage = null
    }
}
