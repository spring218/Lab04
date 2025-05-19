package com.example.movie

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
            
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, MainFragment())
                    .commitNow()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources
        supportFragmentManager.fragments.forEach { fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        }
    }
}

