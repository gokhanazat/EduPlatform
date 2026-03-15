package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Enrollment(
    val userId: String,
    val courseId: String,
    val enrolledAt: String,
    val progressPercent: Int,
    val course: Course? = null
)
