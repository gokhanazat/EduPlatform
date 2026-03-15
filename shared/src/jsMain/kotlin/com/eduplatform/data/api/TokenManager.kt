package com.eduplatform.data.api

import kotlinx.browser.window

actual class TokenManager {
    actual suspend fun saveAccessToken(token: String) {
        window.localStorage.setItem("access_token", token)
    }

    actual suspend fun saveRefreshToken(token: String) {
        window.localStorage.setItem("refresh_token", token)
    }

    actual suspend fun getAccessToken(): String? = window.localStorage.getItem("access_token")

    actual suspend fun getRefreshToken(): String? = window.localStorage.getItem("refresh_token")

    actual suspend fun clearAll() {
        window.localStorage.removeItem("access_token")
        window.localStorage.removeItem("refresh_token")
    }
}
