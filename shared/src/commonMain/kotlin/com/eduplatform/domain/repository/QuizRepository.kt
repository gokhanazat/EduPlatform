package com.eduplatform.domain.repository

import com.eduplatform.domain.model.Quiz
import com.eduplatform.domain.model.QuizAttempt
import com.eduplatform.util.Result

interface QuizRepository {
    suspend fun getQuizForCourse(courseId: String): Result<Quiz>
    suspend fun submitAttempt(attempt: QuizAttempt): Result<QuizAttempt>
    suspend fun getMyAttempts(userId: String, quizId: String): Result<List<QuizAttempt>>
}
