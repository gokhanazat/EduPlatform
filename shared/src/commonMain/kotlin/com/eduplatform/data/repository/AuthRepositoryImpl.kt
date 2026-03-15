package com.eduplatform.data.repository

import com.eduplatform.data.api.*
import com.eduplatform.data.api.dto.*
import com.eduplatform.domain.model.User
import com.eduplatform.domain.repository.AuthRepository
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager,
    private val client: HttpClient
) : AuthRepository {

    private fun isEmail(str: String) = str.contains("@")
    private fun getVirtualEmail(id: String) = if (isEmail(id)) id else "${id.trim()}@tso.local"

    private suspend fun checkWhitelist(identifier: String): Result<Unit> {
        // ADMIN FALLBACK
        if (identifier == "admin@eduplatform.com" || 
            identifier == "gkhnazat@gmail.com" || 
            identifier == "000000") return Result.Success(Unit)

        val isEmail = isEmail(identifier)
        // Veritabanından gelen null değerler için Any? kullanıyoruz
        val whitelistResult: Result<List<WhitelistDto>> = handleApiCall {
            client.get(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                if (isEmail) parameter("email", "eq.$identifier")
                else parameter("sicil_no", "eq.${identifier.trim()}")
                parameter("is_active", "eq.true")
            }
        }
        return when (whitelistResult) {
            is Result.Success -> {
                if (whitelistResult.data.isEmpty()) {
                    Result.Error("Bu hesap aktif değil veya listede bulunamadı.")
                } else Result.Success(Unit)
            }
            is Result.Error -> Result.Error(whitelistResult.message)
            else -> Result.Loading
        }
    }

    override suspend fun signUp(email: String, password: String, fullName: String): Result<User> {
        val whitelistCheck = checkWhitelist(email)
        if (whitelistCheck is Result.Error) return Result.Error(whitelistCheck.message)

        val finalEmail = getVirtualEmail(email)
        return when (val result = api.signUp(finalEmail, password, fullName)) {
            is Result.Success -> signIn(email, password)
            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        val finalEmail = getVirtualEmail(email)
        return when (val result = api.signIn(finalEmail, password)) {
            is Result.Success -> {
                val authResponse = result.data
                tokenManager.saveAccessToken(authResponse.access_token)
                tokenManager.saveRefreshToken(authResponse.refresh_token)
                
                var role = authResponse.user.user_metadata?.role ?: 
                           if (email == "admin@eduplatform.com" || email == "gkhnazat@gmail.com" || email == "000000") "admin" else "student"
                var city = authResponse.user.user_metadata?.city ?: "Merkez"

                if (role != "admin") {
                    val profileResult: Result<List<ProfileDto>> = handleApiCall {
                        client.get(ApiConfig.BASE_URL + ApiConfig.PROFILES) {
                            parameter("id", "eq.${authResponse.user.id}")
                        }
                    }
                    if (profileResult is Result.Success) {
                        val profile = profileResult.data.firstOrNull()
                        role = profile?.role ?: role
                        city = profile?.city ?: city
                    }
                }

                if (role != "admin") {
                    val whitelistCheck = checkWhitelist(email)
                    if (whitelistCheck is Result.Error) {
                        signOut()
                        return Result.Error(whitelistCheck.message)
                    }
                }

                Result.Success(authResponse.user.toDomain(role = role, city = city))
            }
            is Result.Error -> Result.Error("Giriş başarısız. Lütfen bilgilerinizi kontrol edin.")
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun signOut(): Result<Unit> {
        api.signOut()
        tokenManager.clearAll()
        return Result.Success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User?> {
        val token = tokenManager.getAccessToken()
        if (token == null) return Result.Success(null)

        return when (val meResult = api.getMe()) {
            is Result.Success -> {
                val userDto = meResult.data
                var role = userDto.user_metadata?.role ?: "student"
                if (userDto.email == "gkhnazat@gmail.com") role = "admin"
                
                var city = userDto.user_metadata?.city ?: "Merkez"
                
                val profileResult: Result<List<ProfileDto>> = handleApiCall {
                    client.get(ApiConfig.BASE_URL + ApiConfig.PROFILES) {
                        parameter("id", "eq.${userDto.id}")
                    }
                }
                if (profileResult is Result.Success) {
                    val profile = profileResult.data.firstOrNull()
                    role = profile?.role ?: role
                    city = profile?.city ?: city
                }
                Result.Success(userDto.toDomain(role = role, city = city))
            }
            is Result.Error -> Result.Error(meResult.message)
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> = api.resetPassword(getVirtualEmail(email))

    override suspend fun isLoggedIn(): Boolean = tokenManager.getAccessToken() != null

    override suspend fun getWhitelist(search: String?): Result<List<WhitelistDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                parameter("order", "added_at.desc")
                if (!search.isNullOrBlank()) {
                    parameter("or", "(email.ilike.%$search%,sicil_no.ilike.%$search%)")
                }
            }
        }
    }

    override suspend fun addToWhitelist(email: String?, sicilNo: String?, city: String, notes: String): Result<Unit> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                contentType(ContentType.Application.Json)
                setBody(buildMap {
                    if (!email.isNullOrBlank()) put("email", email)
                    if (!sicilNo.isNullOrBlank()) put("sicil_no", sicilNo)
                    put("city", city)
                    put("notes", notes)
                })
            }
        }
    }

    override suspend fun updateWhitelist(id: String, updates: Map<String, Any>): Result<Unit> {
        return handleApiCall {
            client.patch(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                parameter("id", "eq.$id")
                contentType(ContentType.Application.Json)
                setBody(updates)
            }
        }
    }

    override suspend fun removeFromWhitelist(id: String): Result<Unit> {
        return handleApiCall {
            client.delete(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                parameter("id", "eq.$id")
            }
        }
    }
}
