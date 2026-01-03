package com.example.eclinichappy.ui.view.penyakit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.eclinichappy.data.Penyakit
import com.example.eclinichappy.R

// Adapter untuk menampilkan data penyakit dalam bentuk ListView
class PenyakitAdapter(private val context: Context, private val penyakitList: List<Penyakit>) : BaseAdapter() {

    // Mengembalikan jumlah item dalam daftar
    override fun getCount(): Int {
        return penyakitList.size
    }

    // Mendapatkan item pada posisi tertentu
    override fun getItem(position: Int): Any {
        return penyakitList[position]
    }

    // Mendapatkan ID dari item pada posisi tertentu (di sini pakai posisi sebagai ID)
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Menghasilkan tampilan untuk setiap item dalam daftar
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        // Memeriksa apakah tampilan sudah dibuat sebelumnya
        if (convertView == null) {
            // Jika belum, inflasi layout item dan buat ViewHolder baru
            view = LayoutInflater.from(context).inflate(R.layout.item_penyakit, parent, false)
            holder = ViewHolder()
            holder.gambarPenyakit = view.findViewById(R.id.gambar_penyakit) // Gambar penyakit
            holder.namaPenyakit = view.findViewById(R.id.nama_penyakit) // Nama penyakit
            view.tag = holder // Simpan holder di tag untuk penggunaan ulang
        } else {
            // Jika sudah ada, gunakan tampilan yang sudah dibuat
            view = convertView
            holder = view.tag as ViewHolder
        }

        // Mengambil data penyakit berdasarkan posisi
        val penyakit = penyakitList[position]
        holder.namaPenyakit.text = penyakit.namaPenyakit // Menampilkan nama penyakit

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(context)
            .load(penyakit.gambar) // URL gambar
            .into(holder.gambarPenyakit) // Mengatur gambar ke ImageView

        return view
    }

    // ViewHolder untuk menyimpan referensi View agar lebih efisien
    private class ViewHolder {
        lateinit var gambarPenyakit: ImageView // ImageView untuk gambar penyakit
        lateinit var namaPenyakit: TextView // TextView untuk nama penyakit
    }
}
