package com.eduplatform.data.api

import com.eduplatform.data.api.dto.TokenResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createHttpClient(tokenManager: TokenManager): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            encodeDefaults = true
        })
    }

    install(Auth) {
        bearer {
            // Her isteğe anahtarı otomatik ekle (Supabase challenge göndermediği için şart)
            sendWithoutRequest { request ->
                // Sadece Auth API'ları dışındaki (rest v1) isteklere anahtar ekle
                !request.url.encodedPath.contains("/auth/v1/")
            }
            
            loadTokens {
                val accessToken = tokenManager.getAccessToken()
                val refreshToken = tokenManager.getRefreshToken()
                if (accessToken != null && refreshToken != null) {
                    BearerTokens(accessToken, refreshToken)
                } else null
            }
            
            refreshTokens {
                try {
                    val refreshToken = tokenManager.getRefreshToken() ?: return@refreshTokens null
                    
                    // Supabase refresh token isteği
                    val response = client.post(ApiConfig.BASE_URL + ApiConfig.REFRESH) {
                        contentType(ContentType.Application.Json)
                        // ÖNEMLİ: Supabase Body içinde refresh_token bekler
                        setBody(mapOf("refresh_token" to refreshToken))
                        markAsRefreshTokenRequest()
                    }

                    if (response.status == HttpStatusCode.OK) {
                        val resp = response.body<TokenResponse>()
                        tokenManager.saveAccessToken(resp.access_token)
                        tokenManager.saveRefreshToken(resp.refresh_token)
                        BearerTokens(resp.access_token, resp.refresh_token)
                    } else {
                        tokenManager.clearAll()
                        null
                    }
                } catch (e: Exception) {
                    tokenManager.clearAll()
                    null
                }
            }
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }

    defaultRequest {
        header("apikey", ApiConfig.ANON_KEY)
        header("Content-Type", "application/json")
        // Supabase için doğru 'Prefer' başlığı
        header("Prefer", "return=representation")
    }
}

suspend inline fun <reified T> handleApiCall(block: () -> HttpResponse): com.eduplatform.util.Result<T> {
    return try {
        val response = block()
        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.NoContent -> {
                if (T::class == Unit::class) {
                    com.eduplatform.util.Result.Success(Unit as T)
                } else {
                    com.eduplatform.util.Result.Success(response.body<T>())
                }
            }
            HttpStatusCode.Unauthorized -> {
                com.eduplatform.util.Result.Error("Oturum süresi doldu veya yetkisiz erişim. Lütfen giriş yapın.")
            }
            HttpStatusCode.Forbidden -> com.eduplatform.util.Result.Error("Bu işlem için yetkiniz yok (RLS Hatası).")
            HttpStatusCode.NotFound -> com.eduplatform.util.Result.Error("İçerik bulunamadı.")
            else -> {
                val errorBody = try { response.bodyAsText() } catch (e: Exception) { "" }
                com.eduplatform.util.Result.Error("Hata (${response.status.value}): $errorBody")
            }
        }
    } catch (e: Exception) {
        com.eduplatform.util.Result.Error("Bağlantı hatası: ${e.message}")
    }
}
