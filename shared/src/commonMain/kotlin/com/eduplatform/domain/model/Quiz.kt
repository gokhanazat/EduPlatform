package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val id: String,
    val courseId: String,
    val passScorePercent: Int,
    val timeLimitMinutes: Int?,
    val questions: List<Question> = emptyList()
)
