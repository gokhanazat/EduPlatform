package com.eduplatform.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuizDto(
    val id: String,
    val course_id: String,
    val pass_score_percent: Int,
    val time_limit_minutes: Int?,
    val questions: List<QuestionDto> = emptyList()
)

@Serializable
data class QuestionDto(
    val id: String,
    val quiz_id: String,
    val question_text: String,
    val question_type: String,
    val order_index: Int,
    val options: List<OptionDto> = emptyList()
)

@Serializable
data class OptionDto(
    val id: String,
    val question_id: String,
    val option_text: String,
    val is_correct: Boolean
)

@Serializable
data class QuizAttemptDto(
    val id: String? = null,
    val profile_id: String,
    val quiz_id: String,
    val score: Int,
    val passed: Boolean,
    val taken_at: String? = null,
    val answers: Map<String, String> = emptyMap()
)
