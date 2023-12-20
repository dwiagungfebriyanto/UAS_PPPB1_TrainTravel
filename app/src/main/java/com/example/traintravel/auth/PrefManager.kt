package com.example.traintravel.auth

import android.content.Context
import android.content.SharedPreferences

class PrefManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_FILENAME = "AuthAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ID = "id"
        private const val KEY_EMAIL = "email"
        private const val KEY_USERNAME = "username"
        private const val KEY_BIRTHDATE = "birthdate"
        private const val KEY_ROLE = "role"

        @Volatile
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun saveUserId(id: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ID, id)
        editor.apply()
    }

    fun saveEmail(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun saveBirthDate(birthdate: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_BIRTHDATE, birthdate)
        editor.apply()
    }

    fun saveRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ROLE, role)
        editor.apply()
    }

    fun getUserId(): String {
        return sharedPreferences.getString(KEY_ID, "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    fun getUserBirthdate(): String {
        return sharedPreferences.getString(KEY_BIRTHDATE, "") ?: ""
    }

    fun getRole(): String {
        return sharedPreferences.getString(KEY_ROLE, "") ?: ""
    }

    fun isAdmin(): Boolean {
        return getRole() == "admin"
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}