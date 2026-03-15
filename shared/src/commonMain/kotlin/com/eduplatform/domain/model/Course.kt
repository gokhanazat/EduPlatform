package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val city: String, // YENİ: Şehir bilgisi eklendi
    val instructorName: String,
    val durationMinutes: Int,
    val hasCertificate: Boolean,
    val isPublished: Boolean,
    val thumbnailUrl: String?
)
