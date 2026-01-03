package com.example.eclinichappy

import android.content.Context
import android.content.SharedPreferences

// Kelas User dengan metode setter dan getter
class User {
    private var idPengguna: String? = null
    private var level: String? = null

    // Getter dan setter untuk idPengguna
    fun getIdPengguna(): String? {
        return idPengguna
    }

    fun setIdPengguna(idPengguna: String?) {
        this.idPengguna = idPengguna
    }

    // Getter dan setter untuk level pengguna
    fun getLevel(): String? {
        return level
    }

    fun setLevel(level: String?) {
        this.level = level
    }
}

// Kelas SessionHandler untuk mengelola sesi pengguna dalam aplikasi
class SessionHandler(mContext: Context) {
    // Menginisialisasi SharedPreferences untuk menyimpan data sesi
    private val mPreferences: SharedPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val mEditor: SharedPreferences.Editor = mPreferences.edit()

    // Menyimpan informasi pengguna yang masuk
    fun loginUser(id_pengguna: String?, level: String?) {
        mEditor.putString(KEY_ID, id_pengguna)
        mEditor.putString(KEY_LEVEL, level)
        mEditor.apply() // Menyimpan perubahan secara asinkron
    }

    // Mendapatkan detail pengguna dalam bentuk objek User
    val userDetails: User
        get() {
            val user = User()
            user.setIdPengguna(mPreferences.getString(KEY_ID, KEY_EMPTY))
            user.setLevel(mPreferences.getString(KEY_LEVEL, KEY_EMPTY))
            return user
        }

    // Mengecek apakah pengguna sudah login
    fun isLoggedIn(): Boolean {
        val id = mPreferences.getString(KEY_ID, null)
        return id != null // Mengembalikan true jika id_pengguna ada
    }

    // Menghapus data sesi untuk logout
    fun logoutUser() {
        mEditor.clear()
        mEditor.apply() // Menghapus data sesi secara asinkron
    }

    companion object {
        // Konstanta untuk nama SharedPreferences dan kunci-kunci
        private const val PREF_NAME = "UserSession"
        private const val KEY_ID = "id_pengguna"
        private const val KEY_LEVEL = "level"
        private const val KEY_EMPTY = ""
    }
}
