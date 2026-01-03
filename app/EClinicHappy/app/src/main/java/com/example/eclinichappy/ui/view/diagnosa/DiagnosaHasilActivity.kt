@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.diagnosa

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.R
import com.example.eclinichappy.SessionHandler
import com.example.eclinichappy.User
import com.example.eclinichappy.ui.view.penyakit.PenyakitDetailActivity
import org.json.JSONException
import org.json.JSONObject

class DiagnosaHasilActivity : AppCompatActivity() {
    // Deklarasi variabel untuk menyimpan hasil diagnosa, dialog, tombol solusi, dan data user
    private var hasil: String? = null
    private var pDialog: ProgressDialog? = null
    private var btnSolusi: Button? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosa_hasil)

        // Mengatur toolbar dan judul halaman
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Hasil Diagnosa"

        // Mengambil data pengguna dari sesi
        val session = SessionHandler(applicationContext)
        user = session.userDetails

        // Mengambil data hasil diagnosa dari intent
        hasil = intent.extras?.getString("HASIL")

        btnSolusi = findViewById(R.id.btn_solusi) // Menghubungkan tombol solusi dengan layout
        hasilDiagnosa() // Memanggil fungsi untuk memproses hasil diagnosa
    }

    // Menampilkan dialog loading
    private fun displayLoader() {
        pDialog = ProgressDialog(this@DiagnosaHasilActivity)
        pDialog!!.setMessage("Sedang diproses...")
        pDialog!!.isIndeterminate = false
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    @SuppressLint("SetTextI18n")
    private fun hasilDiagnosa() {
        displayLoader() // Menampilkan loading dialog

        val request = JSONObject()
        try {
            // Menyusun request JSON dengan data hasil dan id pengguna
            request.put("hasil", hasil)
            request.put("id_pengguna", user!!.getIdPengguna())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Membuat permintaan POST untuk mendapatkan hasil diagnosa
        val jsArrayRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            request,
            { response ->
                pDialog!!.dismiss() // Menutup loading dialog
                Log.d("DiagnosaHasilActivity", "Response: $response")

                try {
                    if (response.getInt("status") == 0) {
                        // Mengambil data dari response jika status berhasil
                        val idPenyakit = response.getString("id_penyakit")
                        val namaPenyakit = response.getString("nama_penyakit")
                        val persentaseCocok = response.getDouble("persentase_cocok")

                        val tvHasil = findViewById<TextView>(R.id.tv_hasil)
                        val tvPersentase = findViewById<TextView>(R.id.textViewPersentaseCocok)
                        val tvPenyakitLain = findViewById<TextView>(R.id.tvPenyakitLain)

                        // Menampilkan persentase kecocokan
                        tvPersentase.text = "Persentase Kecocokan: $persentaseCocok%"

                        // Logika untuk menampilkan hasil berdasarkan persentase kecocokan
                        if (persentaseCocok >= 40) {
                            tvHasil.text = "Anda kemungkinan besar terkena: $namaPenyakit"
                            btnSolusi!!.visibility = View.VISIBLE
                            btnSolusi!!.setOnClickListener {
                                val myIntent = Intent(this@DiagnosaHasilActivity, PenyakitDetailActivity::class.java)
                                myIntent.putExtra("ID_PENYAKIT", idPenyakit)
                                myIntent.putExtra("nama_penyakit", namaPenyakit) // Mengirimkan nama penyakit
                                startActivity(myIntent)
                            }

                        } else if (persentaseCocok >= 20) {
                            tvHasil.text =
                                "Hasil menunjukkan persentase cukup rendah.\nSilakan pilih gejala lain untuk hasil lebih akurat."
                            btnSolusi!!.visibility = View.GONE
                        } else {
                            tvHasil.text = "Persentase kecocokan sangat rendah. Penyakit tidak dapat ditentukan."
                            btnSolusi!!.visibility = View.GONE
                        }

                        // Menampilkan penyakit lain yang mungkin berdasarkan persentase 20%-40%
                        val penyakitLain = response.getJSONArray("penyakit_lain")
                        val penyakitList = mutableListOf<String>()

                        for (i in 0 until penyakitLain.length()) {
                            val penyakit = penyakitLain.getJSONObject(i)
                            val namaPenyakitLain = penyakit.getString("nama_penyakit")
                            val persentaseLain = penyakit.getDouble("persentase_cocok")

                            if (persentaseLain in 20.0..39.99) {
                                penyakitList.add("$namaPenyakitLain: ${persentaseLain}%")
                            }
                        }

                        tvPenyakitLain.text = if (penyakitList.isNotEmpty()) {
                            "Penyakit lain yang mungkin (20%-40%):\n" + penyakitList.joinToString("\n")
                        } else {
                            "Tidak ada penyakit lain yang terdeteksi."
                        }

                    } else {
                        // Menampilkan pesan error jika status tidak berhasil
                        Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Menampilkan error jika request gagal
                pDialog!!.dismiss()
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        )

        // Menambahkan request ke antrian
        MySingleton.getInstance(this)?.addToRequestQueue(jsArrayRequest)
    }

    companion object {
        // URL API untuk mendapatkan hasil diagnosa
        private const val url = "https://eclinichappy.my.id/android/get_hasil_diagnosa.php"
    }
}
