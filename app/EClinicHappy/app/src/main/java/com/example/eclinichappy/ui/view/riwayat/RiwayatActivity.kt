@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.riwayat

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.R
import com.example.eclinichappy.SessionHandler
import com.example.eclinichappy.User
import com.example.eclinichappy.ui.view.penyakit.PenyakitDetailActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

@Suppress("UNCHECKED_CAST")
class RiwayatActivity : AppCompatActivity() {
    private var pDialog: ProgressDialog? = null
    private var lv: ListView? = null
    private var adapter: SimpleAdapter? = null
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)
        title = "Riwayat Diagnosa"

        // Mengambil data pengguna dari session yang sedang aktif
        val session = SessionHandler(applicationContext)
        user = session.userDetails

        // Inisialisasi ListView untuk menampilkan riwayat diagnosa
        lv = findViewById(R.id.list)
        loadData()

        // Mengatur aksi klik pada item ListView untuk menuju ke halaman detail penyakit
        lv?.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter?.getItem(position) as HashMap<String, String?>
            val idPenyakit = selectedItem["id_penyakit"]
            val namaPenyakit= selectedItem["nama_penyakit"]
            val intent = Intent(this, PenyakitDetailActivity::class.java)
            intent.putExtra("ID_PENYAKIT", idPenyakit)
            intent.putExtra("nama_penyakit",namaPenyakit)
            startActivity(intent)
        }
    }

    // Menampilkan dialog loading ketika data sedang dimuat
    private fun displayLoader() {
        pDialog = ProgressDialog(this).apply {
            setMessage("Sedang diproses...")
            isIndeterminate = false
            setCancelable(false)
            show()
        }
    }

    // Memuat data riwayat diagnosa dari server
    private fun loadData() {
        displayLoader() // Menampilkan dialog loading

        // Mengirimkan request dengan data id pengguna
        val request = JSONObject().apply {
            try {
                put("id_pengguna", user?.getIdPengguna())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Membuat dan mengirim permintaan JSON ke server untuk mendapatkan daftar riwayat
        val jsArrayRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            request,
            { response ->
                pDialog?.dismiss() // Menutup dialog loading setelah respons diterima
                try {
                    if (response.getInt("status") == 0) {
                        // Parsing JSON Array dan memasukkannya ke dalam ArrayList untuk ditampilkan di ListView
                        val jsonArray: JSONArray = response.getJSONArray("riwayat")
                        val list = ArrayList<HashMap<String, String?>>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val map = HashMap<String, String?>().apply {
                                put("nama_penyakit", jsonObject.getString("nama_penyakit"))
                                put("tanggal", jsonObject.getString("tanggal"))
                                put("id_penyakit", jsonObject.getString("id_penyakit")) // Ambil id_penyakit untuk navigasi detail
                            }
                            list.add(map)
                        }

                        // Mengatur adapter untuk menampilkan data riwayat di ListView
                        adapter = SimpleAdapter(
                            this,
                            list,
                            R.layout.riwayat_list,
                            arrayOf("nama_penyakit", "tanggal"),
                            intArrayOf(R.id.tv_hasil, R.id.tv_tanggal)
                        )
                        lv?.adapter = adapter
                    } else {
                        // Tampilkan pesan jika data riwayat tidak tersedia
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

    companion object {
        // URL endpoint untuk mendapatkan data riwayat dari server
        private const val url = "https://eclinichappy.my.id/android/get_daftar_riwayat.php"
    }
}
