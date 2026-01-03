package com.example.eclinichappy.ui.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.eclinichappy.R
import com.example.eclinichappy.SessionHandler
import com.example.eclinichappy.ui.view.login.LoginActivity
import com.example.eclinichappy.ui.view.main.MainActivity

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {
    private var session: SessionHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Inisialisasi SessionHandler untuk mengecek status login pengguna
        session = SessionHandler(applicationContext)

        // Menunda splash screen selama 2 detik
        Handler().postDelayed({
            // Mengecek apakah pengguna sudah login atau belum
            val intent = if (session!!.isLoggedIn()) {
                // Jika sudah login, arahkan ke MainActivity
                Intent(this@SplashScreenActivity, MainActivity::class.java)
            } else {
                // Jika belum login, arahkan ke LoginActivity
                Intent(this@SplashScreenActivity, LoginActivity::class.java)
            }
            // Memulai aktivitas baru berdasarkan status login
            startActivity(intent)
            // Menutup SplashScreenActivity agar tidak bisa diakses kembali
            finish()
        }, 2000) // Waktu tunggu dalam milidetik
    }
}
