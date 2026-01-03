package com.example.eclinichappy.ui.view.main

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import java.util.Timer
import java.util.TimerTask
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eclinichappy.ui.view.login.LoginActivity
import com.example.eclinichappy.R
import com.example.eclinichappy.SessionHandler
import com.example.eclinichappy.ui.view.about.AboutActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    // Data item menu dan gambar yang ditampilkan di RecyclerView
    private val itemname = arrayOf(
        "Diagnosa",
        "Daftar Penyakit",
        "Riwayat Diagnosa",
        "Bantuan"
    )

    private lateinit var customRecyclerAdapter: CustomRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private val imgid = arrayOf(
        R.drawable.diagnosis,
        R.drawable.checklist,
        R.drawable.health_report,
        R.drawable.faqs
    )
    private var session: SessionHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Mengatur Toolbar sebagai AppBar
        val toolbar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Inisialisasi ViewPager2 untuk menampilkan slider gambar
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val images = listOf(R.drawable.ban1, R.drawable.ban2, R.drawable.ban3) // Gambar slider
        val adapt = ImageSliderAdapter(images)
        viewPager.adapter = adapt

        // Inisialisasi RecyclerView untuk menampilkan menu utama
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengatur adapter untuk RecyclerView dengan data itemname dan imgid
        customRecyclerAdapter = CustomRecyclerAdapter(this, itemname, imgid)
        recyclerView.adapter = customRecyclerAdapter

        // Timer untuk mengubah gambar di ViewPager setiap 5 detik
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val currentItem = viewPager.currentItem
                    val nextItem = if (currentItem == images.size - 1) 0 else currentItem + 1
                    viewPager.setCurrentItem(nextItem, true)
                }
            }
        }, 0, 5000)

        // Menghubungkan TabLayout dengan ViewPager agar indikator titik muncul
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        session = SessionHandler(applicationContext)

        // Memeriksa apakah pengguna sudah login
        if (!session!!.isLoggedIn()) {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
    }

    // Menambahkan menu ke toolbar (Toggle Theme, About, Logout)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Menangani aksi klik pada item menu di toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_theme -> {
                // Mengubah tema antara mode gelap dan terang
                toggleTheme()
                true
            }
            R.id.action_about -> {
                // Buka halaman About
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                true
            }
            R.id.action_logout -> {
                // Konfirmasi logout
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Fungsi untuk mengubah tema antara mode terang dan gelap
    private fun toggleTheme() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    // Menangani tombol Back dengan dialog konfirmasi keluar
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey() // Panggil fungsi konfirmasi keluar
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    // Fungsi untuk menampilkan dialog konfirmasi keluar
    private fun exitByBackKey() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Anda yakin mau keluar?")
            .setPositiveButton("Ya") { _, _ -> finish() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Fungsi untuk logout dengan dialog konfirmasi
    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage("Anda yakin mau logout?")
            .setPositiveButton("Ya, Logout") { _, _ ->
                // Hapus sesi login dan kembali ke LoginActivity
                session?.logoutUser()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
