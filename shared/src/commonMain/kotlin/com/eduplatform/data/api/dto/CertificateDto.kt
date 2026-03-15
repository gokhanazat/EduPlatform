package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CertificateDto(
    val id: String,
    val user_id: String,
    val course_id: String,
    val course_title: String,
    val user_name: String,
    val issued_at: String,
    val verify_code: String,
    val pdf_url: String?
)
