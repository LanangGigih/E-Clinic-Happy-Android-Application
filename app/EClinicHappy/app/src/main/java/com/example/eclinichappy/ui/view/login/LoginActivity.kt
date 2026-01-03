@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.login

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale
import android.animation.ObjectAnimator
import android.widget.ImageView
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.R
import com.example.eclinichappy.ui.view.register.RegisterActivity
import com.example.eclinichappy.SessionHandler
import com.example.eclinichappy.ui.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private var etUsername: EditText? = null // Input field untuk username
    private var etPassword: EditText? = null // Input field untuk password
    private var username: String? = null // Variabel untuk menyimpan username
    private var password: String? = null // Variabel untuk menyimpan password
    private var pDialog: ProgressDialog? = null
    private var session: SessionHandler? = null // Handler untuk session login

    // URL untuk endpoint login
    private val loginUrl: String = "https://eclinichappy.my.id/android/login.php"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Login E-Clinic Happy"

        session = SessionHandler(applicationContext)

        // Memeriksa apakah pengguna sudah login, jika ya, langsung ke menu utama
        if (session?.isLoggedIn() == true) {
            val level = session?.userDetails?.getLevel()
            loadMainMenu(level ?: "")
            return
        }

        // Menghubungkan UI dengan variabel
        etUsername = findViewById(R.id.edt_email_login)
        etPassword = findViewById(R.id.edt_pass_login)
        val login: Button = findViewById(R.id.btn_login)
        val register: Button = findViewById(R.id.btn_regis)
        val loginImage = findViewById<ImageView>(R.id.img_login)

        // Menambahkan animasi gambar pada halaman login
        val translationX = ObjectAnimator.ofFloat(loginImage, "translationX", -50f, 50f)
        translationX.duration = 6000 // Durasi animasi 6 detik
        translationX.repeatCount = ObjectAnimator.INFINITE // Animasi terus berulang
        translationX.repeatMode = ObjectAnimator.REVERSE // Animasi bolak-balik
        translationX.start()

        // Menangani klik pada tombol login
        login.setOnClickListener {
            username = etUsername?.text?.toString()?.lowercase(Locale.getDefault())?.trim()
            password = etPassword?.text?.toString()?.trim()

            if (validateInputs()) {
                login() // Memanggil fungsi login
            }
        }

        // Menangani klik pada tombol register
        register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish() // Menutup LoginActivity saat berpindah ke RegisterActivity
        }
    }

    // Fungsi untuk menavigasi ke menu utama berdasarkan level pengguna
    private fun loadMainMenu(level: String) {
        val intent = when (level) {
            "User" -> Intent(applicationContext, MainActivity::class.java)
            else -> return
        }
        startActivity(intent)
        finish()
    }

    // Fungsi untuk menampilkan loader
    private fun displayLoader() {
        pDialog = ProgressDialog(this@LoginActivity).apply {
            setMessage("Sedang diproses...")
            isIndeterminate = false
            setCancelable(false)
            show()
        }
    }

    // Fungsi login untuk memeriksa kredensial pengguna
    private fun login() {
        displayLoader()

        val request = JSONObject()
        try {
            request.put("username", username)
            request.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsArrayRequest = JsonObjectRequest(
            Request.Method.POST,
            loginUrl,
            request,
            { response ->
                pDialog?.dismiss() // Menutup dialog saat respons diterima
                try {
                    if (response.getInt("status") == 0) {
                        // Menyimpan sesi login jika berhasil
                        session?.loginUser(
                            response.getString("id_pengguna"),
                            response.getString("level")
                        )
                        loadMainMenu(response.getString("level")) // Mengakses menu utama sesuai level
                    } else {
                        Toast.makeText(
                            applicationContext,
                            response.getString("message"), Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                pDialog?.dismiss()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            })

        MySingleton.getInstance(this)?.addToRequestQueue(jsArrayRequest)
    }

    // Validasi input untuk memastikan username dan password terisi
    private fun validateInputs(): Boolean {
        if (username.isNullOrEmpty()) {
            etUsername?.error = "Isi dulu Username"
            etUsername?.requestFocus()
            return false
        }
        if (password.isNullOrEmpty()) {
            etPassword?.error = "Isi dulu Password"
            etPassword?.requestFocus()
            return false
        }
        return true
    }
}
