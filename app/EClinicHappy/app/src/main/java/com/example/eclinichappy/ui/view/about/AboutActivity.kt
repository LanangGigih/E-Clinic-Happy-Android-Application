package com.example.eclinichappy.ui.view.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eclinichappy.R

// Activity untuk menampilkan informasi tentang aplikasi
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about) // Mengatur layout untuk activity

        // Mengatur judul toolbar
        title = "About"
    }
}
