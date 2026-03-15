package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizAttempt(
    val id: String = "",
    val userId: String,
    val quizId: String,
    val score: Int,
    val passed: Boolean,
    val takenAt: String = "",
    val answers: Map<String, String> = emptyMap()
)
