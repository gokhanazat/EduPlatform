package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    val id: String,
    val course_id: String,
    val title: String,
    val content_type: String,
    val content_markdown: String?,
    val video_url: String?,
    val order_index: Int
)

@Serializable
data class LessonProgressDto(
    val profile_id: String,
    val lesson_id: String
)
