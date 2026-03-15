package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val contentType: String,  // "text" or "video"
    val contentMarkdown: String?,
    val videoUrl: String?,
    val orderIndex: Int
)
