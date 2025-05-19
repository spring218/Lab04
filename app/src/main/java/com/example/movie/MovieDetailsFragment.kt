package com.example.movie

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.movie.model.Movie
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.movie.repository.MovieRepository
import com.example.movie.ui.MovieDetailsBackgroundState

class MovieDetailsFragment : DetailsSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var movie: Movie
    private lateinit var backgroundState: MovieDetailsBackgroundState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        movie = requireActivity().intent.getParcelableExtra("movie")!!
        backgroundState = MovieDetailsBackgroundState(requireActivity())
        setupUi()
        setupDetailsOverviewRow()
        loadBackdrop()
    }

    private fun setupUi() {
        title = movie.title
    }

    private fun setupDetailsOverviewRow() {
        // Set up adapter
        val presenterSelector = ClassPresenterSelector()
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter(),
            DetailsOverviewLogoPresenter()
        ).apply {
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.black)
            initialState = FullWidthDetailsOverviewRowPresenter.STATE_FULL
        }

        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        rowsAdapter = ArrayObjectAdapter(presenterSelector)

        // Create details overview row
        val row = DetailsOverviewRow(movie)

        // Load movie poster
        val posterUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        Glide.with(requireActivity())
            .asBitmap()
            .load(posterUrl)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    row.setImageBitmap(requireActivity(), bitmap)
                    rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size())
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })

        // Add actions
        val actionsAdapter = ArrayObjectAdapter().apply {
            add(Action(0, "Watch Now"))
            add(Action(1, "Add to Favorites"))
            add(Action(2, "Watch Trailer"))
        }
        row.actionsAdapter = actionsAdapter

        rowsAdapter.add(row)
        adapter = rowsAdapter

        // Set click listener for actions
        detailsPresenter.setOnActionClickedListener { action ->
            when (action.id.toInt()) {
                0 -> Toast.makeText(context, "Watch Now Clicked", Toast.LENGTH_SHORT).show()
                1 -> {
                    lifecycleScope.launch {
                        try {
                            val repository = MovieRepository()
                            repository.markAsFavorite(movie.id, true)
                            Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to add to favorites: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                2 -> Toast.makeText(context, "Watch Trailer Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBackdrop() {
        val backdropUrl = "https://image.tmdb.org/t/p/w1280${movie.backdropPath}"
        val width = requireActivity().window.decorView.width
        val height = requireActivity().window.decorView.height

        Glide.with(requireActivity())
            .asBitmap()
            .load(backdropUrl)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>(width, height) {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    backgroundState.setBitmap(bitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    backgroundState.setBitmap(null)
                }
            })
    }
}

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    override fun onBindDescription(vh: ViewHolder, item: Any) {
        val movie = item as Movie
        vh.title.text = movie.title
        vh.subtitle.text = "Release Date: ${movie.releaseDate}"
        vh.body.text = movie.overview
    }
}
