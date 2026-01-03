package com.example.eclinichappy.ui.view.penyakit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.data.Penyakit
import com.example.eclinichappy.R
import org.json.JSONArray
import org.json.JSONException

class PenyakitActivity : AppCompatActivity() {

    private lateinit var listView: ListView // ListView untuk menampilkan daftar penyakit
    private lateinit var penyakitAdapter: PenyakitAdapter // Adapter untuk menghubungkan data ke ListView
    private val penyakitList = ArrayList<Penyakit>() // ArrayList untuk menyimpan data penyakit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penyakit)

        listView = findViewById(R.id.list) // Inisialisasi ListView
        loadPenyakitData() // Memuat data penyakit dari API

        // Mengatur klik pada item ListView untuk membuka detail penyakit
        listView.setOnItemClickListener { _, _, position, _ ->
            val penyakit = penyakitList[position]
            val intent = Intent(this, PenyakitDetailActivity::class.java).apply {
                // Mengirim data ID, nama penyakit, dan gambar ke PenyakitDetailActivity
                putExtra("ID_PENYAKIT", penyakit.idPenyakit) // Pastikan menggunakan ID_PENYAKIT
                putExtra("nama_penyakit", penyakit.namaPenyakit)
                putExtra("gambar", penyakit.gambar)
            }
            startActivity(intent) // Memulai PenyakitDetailActivity
        }
    }

    // Fungsi untuk mengambil data penyakit dari API
    private fun loadPenyakitData() {
        val url = "https://eclinichappy.my.id/android/get_daftar_penyakit.php" // URL API

        // Membuat permintaan JSON dengan metode GET
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("PenyakitActivity", "Response: $response") // Log respons untuk debugging

                // Memeriksa apakah respons berisi data penyakit
                if (response.has("penyakit")) {
                    try {
                        val penyakitArray: JSONArray = response.getJSONArray("penyakit")
                        for (i in 0 until penyakitArray.length()) {
                            val penyakitObject = penyakitArray.getJSONObject(i)
                            val penyakit = Penyakit(
                                penyakitObject.getString("id_penyakit"),
                                penyakitObject.getString("nama_penyakit"),
                                penyakitObject.optString("gambar")
                            )
                            penyakitList.add(penyakit) // Menambahkan data penyakit ke ArrayList
                        }
                        // Inisialisasi adapter dan menghubungkan data dengan ListView
                        penyakitAdapter = PenyakitAdapter(this, penyakitList)
                        listView.adapter = penyakitAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Tampilkan pesan jika data 'penyakit' tidak ditemukan
                    Log.e("PenyakitActivity", "No 'penyakit' key in the response")
                    Toast.makeText(this, "Data penyakit tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            },
            { error: VolleyError ->
                // Tampilkan pesan jika terjadi kesalahan saat mengambil data
                Toast.makeText(this, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
            })
        MySingleton.getInstance(this)?.addToRequestQueue(jsonObjectRequest) // Menambahkan permintaan ke antrean
    }
}
