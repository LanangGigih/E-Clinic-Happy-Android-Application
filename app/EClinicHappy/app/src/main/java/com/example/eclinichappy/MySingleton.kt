package com.example.eclinichappy

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

// Singleton untuk mengelola RequestQueue Volly yang digunakan di seluruh aplikasi
class MySingleton private constructor(private var mCtx: Context) {
    private var mRequestQueue: RequestQueue?

    init {
        // Inisialisasi RequestQueue saat instance pertama kali dibuat
        mRequestQueue = requestQueue
    }

    // Getter untuk RequestQueue, membuatnya jika belum diinisialisasi
    private val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
            }
            return mRequestQueue!!
        }

    // Menambahkan permintaan ke RequestQueue
    fun <T> addToRequestQueue(req: Request<T>?) {
        requestQueue.add(req)
    }

    companion object {
        // Instance tunggal dari MySingleton untuk memastikan hanya ada satu RequestQueue
        @SuppressLint("StaticFieldLeak")
        private var mInstance: MySingleton? = null

        // Mendapatkan instance MySingleton atau membuatnya jika belum ada
        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (mInstance == null) {
                mInstance = MySingleton(context)
            }
            return mInstance
        }
    }
}
