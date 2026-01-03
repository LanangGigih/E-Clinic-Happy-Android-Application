package com.example.eclinichappy.ui.view.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eclinichappy.ui.view.diagnosa.DiagnosaActivity
import com.example.eclinichappy.ui.view.penyakit.PenyakitActivity
import com.example.eclinichappy.R
import com.example.eclinichappy.ui.view.riwayat.RiwayatActivity
import com.example.eclinichappy.ui.view.bantuan.BantuanUserActivity

@Suppress("DEPRECATION")
class CustomRecyclerAdapter(
    private val context: Context,
    private val itemNames: Array<String>, // Array untuk nama item menu
    private val imgIds: Array<Int> // Array untuk ID gambar item menu
) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    // ViewHolder untuk menyimpan dan mengatur tampilan item dalam RecyclerView
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.menu_text) // Menampilkan nama item
        val itemImage: ImageView = itemView.findViewById(R.id.menu_icon) // Menampilkan ikon item

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Menangani klik berdasarkan posisi item
                    when (position) {
                        0 -> context.startActivity(Intent(context, DiagnosaActivity::class.java)) // Klik pada item pertama akan membuka DiagnosaActivity
                        1 -> context.startActivity(Intent(context, PenyakitActivity::class.java)) // Klik pada item kedua akan membuka PenyakitActivity
                        2 -> context.startActivity(Intent(context, RiwayatActivity::class.java)) // Klik pada item ketiga akan membuka RiwayatActivity
                        3 -> context.startActivity(Intent(context, BantuanUserActivity::class.java)) // Klik pada item keempat akan membuka BantuanUserActivity
                    }
                }
            }
        }
    }

    // Fungsi untuk mengatur tampilan setiap item pada RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate layout untuk setiap item dalam RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycle_item, parent, false)
        return ViewHolder(view)
    }

    // Mengikat data pada item saat RecyclerView menampilkan item pada posisi tertentu
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = itemNames[position] // Mengatur teks nama item
        holder.itemImage.setImageResource(imgIds[position]) // Mengatur ikon item
    }

    // Mengembalikan jumlah item dalam adapter
    override fun getItemCount(): Int {
        return itemNames.size
    }
}
