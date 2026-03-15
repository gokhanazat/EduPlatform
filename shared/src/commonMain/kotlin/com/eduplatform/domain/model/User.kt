package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val sicilNo: String,
    val city: String,
    val role: String = "student"
)
