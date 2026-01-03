@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.penyakit

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import androidx.appcompat.widget.Toolbar
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.R
import java.net.URLEncoder

class PenyakitDetailActivity : AppCompatActivity() {
    private var pDialog: ProgressDialog? = null
    private lateinit var namaPenyakit: TextView
    private lateinit var detailPenyakit: TextView
    private lateinit var solusiPenyakit: TextView
    private lateinit var gambarPenyakit: ImageView
    private var idPenyakit: String? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penyakit_detail)


        val namaPnykt = intent.getStringExtra("nama_penyakit") ?: "penyakit tidak dikenal"

        val btnHubungiDokter: ImageButton = findViewById(R.id.btn_kontak_dokter)
        btnHubungiDokter.setOnClickListener {
            hubungiDokter(namaPnykt)
        }



        // Inisialisasi toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Informasi Penyakit"

        progressBar = findViewById(R.id.progress_bar)

        // Inisialisasi tampilan untuk data penyakit
        namaPenyakit = findViewById(R.id.nama_penyakit)
        detailPenyakit = findViewById(R.id.detail_penyakit)
        solusiPenyakit = findViewById(R.id.solusi_penyakit)
        gambarPenyakit = findViewById(R.id.gambar_penyakit)

        // Mengambil ID penyakit dari intent
        val extras = intent.extras
        if (extras != null) {
            idPenyakit = extras.getString("ID_PENYAKIT")
        } else {
            Toast.makeText(this, "ID Penyakit tidak ditemukan", Toast.LENGTH_SHORT).show()
            return // Kembali jika ID tidak ditemukan
        }

        // Ambil data penyakit dari API
        fetchData()
    }

    // Menampilkan loader selama permintaan data berlangsung
    private fun displayLoader() {
        if (!isFinishing) {
            pDialog = ProgressDialog(this@PenyakitDetailActivity)
            pDialog!!.setMessage("Sedang diproses...")
            pDialog!!.isIndeterminate = false
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }
    }

    private fun hubungiDokter(namaPenyakit: String) {
        val nomorTelepon = "+6287763442953" // Ganti dengan nomor dokter
        val pesan = "Halo dokter, saya ingin bertanya tentang penyakit $namaPenyakit"
        val pesanTerencode = URLEncoder.encode(pesan, "UTF-8")

        val url = "https://wa.me/$nomorTelepon?text=$pesanTerencode"


        // Intent untuk membuka WhatsApp
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }


        // Mengambil data penyakit dari server API
    @SuppressLint("SetTextI18n")
    private fun fetchData() {
        displayLoader() // Menampilkan dialog loading

        val request = JSONObject()
        try {
            request.put("id_penyakit", idPenyakit) // Menambahkan ID penyakit ke JSON request
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Membuat permintaan untuk mendapatkan data penyakit
        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, url, request, { response ->
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss() // Menutup dialog setelah mendapat respons
            }
            try {
                if (response.getInt("status") == 0) {
                    // Menampilkan data penyakit yang diterima
                    namaPenyakit.text = response.getString("nama_penyakit")
                    detailPenyakit.text = """
                    ${response.getString("deskripsi")}
                    """.trimIndent()
                    solusiPenyakit.text = """
                    ${response.getString("solusi")}
                    """.trimIndent()

                    // Memuat gambar dari URL menggunakan Glide
                    val gambarUrl = response.getString("gambar")
                    Glide.with(this)
                        .load(gambarUrl)
                        .into(gambarPenyakit)
                } else {
                    Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error ->
            if (pDialog != null && pDialog!!.isShowing) {
                pDialog!!.dismiss()
            }
            // Menangani error pada permintaan data
            Toast.makeText(applicationContext, error.message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
        }

        // Menambahkan permintaan ke antrean request
        MySingleton.getInstance(this)?.addToRequestQueue(jsArrayRequest)
    }

    // Menangani tombol kembali pada toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        // URL untuk mendapatkan data penyakit
        private const val url = "https://eclinichappy.my.id/android/get_penyakit.php"
    }
}
