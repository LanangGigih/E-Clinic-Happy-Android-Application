package com.example.eclinichappy.ui.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.eclinichappy.R

// Adapter untuk menampilkan gambar slider menggunakan RecyclerView
class ImageSliderAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    // ViewHolder untuk memegang tampilan item pada slider
    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView) // Menampilkan gambar pada slider
    }

    // Membuat tampilan item untuk slider
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(itemView)
    }

    // Mengatur gambar yang ditampilkan di slider berdasarkan posisi
    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position]) // Mengatur sumber gambar dari daftar 'images'
    }

    // Mengembalikan jumlah total gambar pada slider
    override fun getItemCount(): Int {
        return images.size
    }
}
