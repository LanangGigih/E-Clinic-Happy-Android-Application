@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.register

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
import com.example.eclinichappy.ui.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private var email: String? = null
    private var username: String? = null
    private var password: String? = null
    private var pDialog: ProgressDialog? = null
    private val url: String = "https://eclinichappy.my.id/android/register.php"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = "Register E-Clinic Happy"

        // Inisialisasi input field
        etEmail = findViewById(R.id.edt_email)
        etUsername = findViewById(R.id.edt_username)
        etPassword = findViewById(R.id.edt_pass)

        // Inisialisasi tombol login dan register
        val login: Button = findViewById(R.id.btn_login)
        val register: Button = findViewById(R.id.btn_register)

        // Animasi gerakan gambar secara horizontal
        val regisImage = findViewById<ImageView>(R.id.img_register)
        val translationX = ObjectAnimator.ofFloat(regisImage, "translationX", -50f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        translationX.start()

        // Navigasi ke LoginActivity ketika tombol login diklik
        login.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Mendaftarkan pengguna ketika tombol register diklik
        register.setOnClickListener {
            email = etEmail.text.toString().trim()
            username = etUsername.text.toString().lowercase(Locale.getDefault()).trim()
            password = etPassword.text.toString().trim()

            if (validateInputs()) { // Memastikan input valid sebelum memulai proses registrasi
                register()
            }
        }
    }

    // Menampilkan dialog proses saat registrasi sedang berlangsung
    private fun displayLoader() {
        pDialog = ProgressDialog(this).apply {
            setMessage("Sedang diproses...")
            isIndeterminate = false
            setCancelable(false)
            show()
        }
    }

    // Fungsi untuk mengirim data registrasi ke server
    private fun register() {
        displayLoader() // Tampilkan dialog loading

        // Membuat JSON object untuk request data
        val request = JSONObject().apply {
            try {
                put("username", username)
                put("password", password)
                put("email", email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Membuat dan mengirim permintaan JSON ke server
        val jsArrayRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            request,
            { response ->
                pDialog?.dismiss() // Menutup dialog setelah mendapat respons
                try {
                    if (response.getInt("status") == 0) {
                        // Jika registrasi berhasil, beri pesan sukses dan pindah ke LoginActivity
                        Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Tampilkan pesan jika registrasi gagal
                        Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                pDialog?.dismiss()
                // Tampilkan pesan kesalahan jika terjadi error dalam permintaan
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        )

        // Menambahkan permintaan ke antrean request
        MySingleton.getInstance(this)?.addToRequestQueue(jsArrayRequest)
    }

    // Validasi input pengguna
    private fun validateInputs(): Boolean {
        if (email.isNullOrEmpty()) {
            etEmail.error = "Isi dulu Email"
            etEmail.requestFocus()
            return false
        }
        if (username.isNullOrEmpty()) {
            etUsername.error = "Isi dulu Username"
            etUsername.requestFocus()
            return false
        }
        if (password.isNullOrEmpty()) {
            etPassword.error = "Isi dulu Password"
            etPassword.requestFocus()
            return false
        }
        return true
    }
}
