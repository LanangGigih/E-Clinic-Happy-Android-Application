@file:Suppress("DEPRECATION")

package com.example.eclinichappy.ui.view.diagnosa

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.eclinichappy.MySingleton
import com.example.eclinichappy.R
import com.example.eclinichappy.data.Gejala
import org.json.JSONException

// Activity untuk memilih gejala dalam proses diagnosa
class DiagnosaActivity : AppCompatActivity() {
    private var pDialog: ProgressDialog? = null
    private var dataAdapter: MyCustomAdapter? = null
    private var gejalaList = ArrayList<Gejala>() // List gejala yang akan ditampilkan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosa)
        title = "Pilih Gejala" // Set judul halaman
        daftarGejala() // Ambil data gejala dari server

        // Tombol untuk melakukan diagnosa
        val btn_diagnosa = findViewById<Button>(R.id.btn_diagnosa)
        btn_diagnosa.setOnClickListener { v ->
            val responseText = StringBuffer()

            // Mengecek apakah ada gejala yang dipilih
            if (gejalaList.isNotEmpty()) {
                val gejalaList2 = dataAdapter!!.gejalaList
                for (gejala in gejalaList2) {
                    if (gejala.isSelected) {
                        responseText.append(gejala.name + "#") // Menyimpan gejala yang dipilih
                    }
                }
            }

            // Tampilkan pesan jika belum ada gejala yang dipilih
            if (responseText.isEmpty()) {
                Toast.makeText(this, "Pilih dulu gejala minimal 2-3 Gejala!", Toast.LENGTH_SHORT).show()
            } else {
                // Kirim hasil pilihan gejala ke DiagnosaHasilActivity
                val myIntent = Intent(v.context, DiagnosaHasilActivity::class.java)
                myIntent.putExtra("HASIL", responseText.toString())
                startActivity(myIntent)
            }
        }
    }

    // Menampilkan dialog loader saat data sedang diproses
    private fun displayLoader() {
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Sedang diproses...")
        pDialog!!.isIndeterminate = false
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    // Menampilkan ListView untuk gejala
    private fun displayGejalaView() {
        dataAdapter = MyCustomAdapter(this, R.layout.diagnosa_list, gejalaList)
        val listView = findViewById<ListView>(R.id.list)
        listView.adapter = dataAdapter
    }

    // Mengambil daftar gejala dari server
    private fun daftarGejala() {
        displayLoader() // Menampilkan loader
        val jsArrayRequest = JsonObjectRequest(Request.Method.POST, url, null, { response ->
            pDialog!!.dismiss()
            try {
                if (response.getInt("status") == 0) {
                    gejalaList = ArrayList() // Reset list gejala
                    val jsonArray = response.getJSONArray("gejala")

                    // Loop melalui data gejala dari server
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("nama_gejala")
                        val gejala = Gejala(name, false) // Inisialisasi gejala
                        gejalaList.add(gejala)
                    }
                    displayGejalaView() // Tampilkan list gejala
                } else {
                    Toast.makeText(applicationContext, response.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error ->
            pDialog!!.dismiss()
            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
        }
        MySingleton.getInstance(this)!!.addToRequestQueue(jsArrayRequest) // Mengirim request ke server
    }

    // Custom adapter untuk menghubungkan data gejala dengan tampilan ListView
    private inner class MyCustomAdapter(
        context: Context,
        textViewResourceId: Int,
        gejalaList: MutableList<Gejala>
    ) : ArrayAdapter<Gejala>(context, textViewResourceId, gejalaList) {
        val gejalaList: MutableList<Gejala> = ArrayList(gejalaList) // Menyimpan list gejala

        // ViewHolder pattern untuk mengoptimalkan ListView
        private inner class ViewHolder {
            var name: CheckBox? = null
        }

        // Menghubungkan data gejala dengan view di ListView
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val holder: ViewHolder
            val view: View

            if (convertView == null) {
                // Inflate layout jika belum ada view yang didaur ulang
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.diagnosa_list, parent, false)
                holder = ViewHolder()
                holder.name = view.findViewById(R.id.chk_gejala) // Ambil elemen CheckBox
                view.tag = holder

                // Set listener untuk setiap CheckBox
                holder.name?.setOnClickListener { v ->
                    val cb = v as CheckBox
                    val gejala = cb.tag as Gejala
                    gejala.isSelected = cb.isChecked // Tandai gejala yang dipilih
                }
            } else {
                view = convertView
                holder = convertView.tag as ViewHolder
            }

            val gejala = gejalaList[position]
            holder.name?.text = gejala.name // Set nama gejala di CheckBox
            holder.name?.isChecked = gejala.isSelected // Set status checkbox sesuai gejala
            holder.name?.tag = gejala // Tag untuk identifikasi gejala

            return view
        }
    }

    // URL untuk mengambil data gejala dari server
    companion object {
        private const val url = "https://eclinichappy.my.id/android/get_daftar_gejala.php"
    }
}
