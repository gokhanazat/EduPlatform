package com.eduplatform.data.repository

import com.eduplatform.data.api.QuizApiService
import com.eduplatform.db.EduDatabase
import com.eduplatform.domain.model.Quiz
import com.eduplatform.domain.model.QuizAttempt
import com.eduplatform.domain.repository.QuizRepository
import com.eduplatform.util.Result

class QuizRepositoryImpl(
    private val api: QuizApiService,
    private val db: EduDatabase
) : QuizRepository {

    override suspend fun getQuizForCourse(courseId: String): Result<Quiz> {
        return api.getQuiz(courseId).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun submitAttempt(attempt: QuizAttempt): Result<QuizAttempt> {
        // Map domain to DTO for submission
        val dto = com.eduplatform.data.api.dto.QuizAttemptDto(
            user_id = attempt.userId,
            quiz_id = attempt.quizId,
            score = attempt.score,
            passed = attempt.passed,
            answers = attempt.answers
        )
        return api.submitAttempt(dto).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun getMyAttempts(userId: String, quizId: String): Result<List<QuizAttempt>> {
        return api.getMyAttempts(userId, quizId).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }
}
