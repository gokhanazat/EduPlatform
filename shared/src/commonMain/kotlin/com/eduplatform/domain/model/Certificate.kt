package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Certificate(
    val id: String,
    val userId: String,
    val courseId: String,
    val courseTitle: String,
    val userName: String,
    val issuedAt: String,
    val verifyCode: String,
    val pdfUrl: String?
)
