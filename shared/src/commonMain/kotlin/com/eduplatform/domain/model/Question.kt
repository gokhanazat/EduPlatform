package com.eduplatform.domain.model

import kotlinx.serialization.Serializable

enum class QuestionType { MULTIPLE_CHOICE, TRUE_FALSE }

@Serializable
data class Question(
    val id: String,
    val quizId: String,
    val questionText: String,
    val type: QuestionType,
    val orderIndex: Int,
    val options: List<QuestionOption> = emptyList()
)

@Serializable
data class QuestionOption(
    val id: String,
    val questionId: String,
    val optionText: String,
    val isCorrect: Boolean
)
