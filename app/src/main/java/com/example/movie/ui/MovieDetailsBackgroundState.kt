package com.example.movie.ui

import android.app.Activity
import android.graphics.Bitmap
import androidx.leanback.app.BackgroundManager

class MovieDetailsBackgroundState(private val activity: Activity) {
    private var backgroundManager: BackgroundManager? = null
    
    init {
        prepareBackgroundManager()
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity).apply {
            attach(activity.window)
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        backgroundManager?.setBitmap(bitmap)
    }

    fun release() {
        backgroundManager?.release()
    }
}
