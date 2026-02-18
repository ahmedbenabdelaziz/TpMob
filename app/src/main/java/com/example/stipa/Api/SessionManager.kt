package com.example.stipa

import android.content.Context
import android.content.SharedPreferences
// Nous utilisons SharedPreferences pour stocker le token
// car nous avons rencontré un problème avec Jetpack DataStore
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

}
