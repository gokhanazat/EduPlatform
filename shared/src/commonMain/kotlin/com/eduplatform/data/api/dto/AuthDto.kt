package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val user_metadata: UserMetadataDto? = null
)

@Serializable
data class UserMetadataDto(
    val full_name: String? = null,
    val sicil_no: String? = null,
    val role: String? = null,
    val city: String? = null
)

@Serializable
data class TokenResponse(
    val access_token: String,
    val refresh_token: String
)
@Serializable
data class ProfileDto(
    val id: String? = null,
    val role: String? = null,
    val city: String? = null,
    val full_name: String? = null,
    val sicil_no: String? = null
)

@Serializable
data class WhitelistDto(
    val id: String? = null,
    val email: String? = null,
    val sicil_no: String? = null,
    val city: String? = null,
    val is_active: Boolean? = true,
    val added_at: String? = null,
    val notes: String? = null
)
