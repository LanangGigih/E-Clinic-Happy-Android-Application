package com.example.eclinichappy.ui.view.bantuan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eclinichappy.R

// Activity untuk menampilkan informasi tentang aplikasi
class BantuanUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bantuan_user)//layout untuk activity

        //judul toolbar
        title = "Bantuan"
    }
}