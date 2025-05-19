package com.example.movie

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class MovieDetailsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.movie_details_fragment, MovieDetailsFragment())
                .commit()
        }
    }
}
