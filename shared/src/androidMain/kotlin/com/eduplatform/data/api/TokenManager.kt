package com.eduplatform.data.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual suspend fun saveAccessToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    actual suspend fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    actual suspend fun getAccessToken(): String? = prefs.getString("access_token", null)

    actual suspend fun getRefreshToken(): String? = prefs.getString("refresh_token", null)

    actual suspend fun clearAll() {
        prefs.edit().clear().apply()
    }
}
