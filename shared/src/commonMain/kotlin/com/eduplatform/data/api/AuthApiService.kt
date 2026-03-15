package com.eduplatform.data.api

import com.eduplatform.data.api.dto.*
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {

    suspend fun signUp(email: String, password: String, fullName: String): Result<AuthResponse> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.SIGN_UP) {
                setBody(SignUpRequest(
                    email = email,
                    password = password,
                    data = SignUpData(fullName, email.substringBefore("@"))
                ))
            }
        }
    }

    suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.SIGN_IN) {
                setBody(SignInRequest(email, password))
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.SIGN_OUT)
        }
    }

    suspend fun getMe(): Result<UserDto> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.ME)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.RESET) {
                setBody(ResetPasswordRequest(email))
            }
        }
    }
}
