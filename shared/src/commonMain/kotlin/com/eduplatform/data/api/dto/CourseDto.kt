package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: String? = null,
    val title: String,
    val description: String,
    val category: String,
    val city: String? = null, // YENİ: Şehir bilgisi eklendi
    val instructor_name: String,
    val duration_minutes: Int,
    val has_certificate: Boolean,
    val is_published: Boolean,
    val thumbnail_url: String? = null
)

@Serializable
data class EnrollmentDto(
    val user_id: String,
    val course_id: String,
    val enrolled_at: String? = null,
    val progress_percent: Int = 0,
    val courses: CourseDto? = null
)
