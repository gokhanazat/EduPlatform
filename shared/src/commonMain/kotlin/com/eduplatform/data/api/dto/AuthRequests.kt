package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val email: String,
    val password: String
)

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String,
    val data: SignUpData
)

@Serializable
data class SignUpData(
    val full_name: String,
    val sicil_no: String
)

@Serializable
data class RefreshRequest(
    val refresh_token: String
)

@Serializable
data class ResetPasswordRequest(
    val email: String
)
