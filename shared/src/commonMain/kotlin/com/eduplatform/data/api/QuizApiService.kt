package com.eduplatform.data.api

import com.eduplatform.data.api.dto.QuizAttemptDto
import com.eduplatform.data.api.dto.QuizDto
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class QuizApiService(private val client: HttpClient) {

    suspend fun getQuiz(courseId: String): Result<QuizDto> {
        return handleApiCall<List<QuizDto>> {
            client.get(ApiConfig.BASE_URL + ApiConfig.QUIZZES) {
                parameter("course_id", "eq.$courseId")
                parameter("select", "*,questions(*,options(*))")
            }
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val quiz = result.data.firstOrNull()
                    if (quiz != null) Result.Success(quiz)
                    else Result.Error("İçerik bulunamadı.")
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    suspend fun submitAttempt(dto: QuizAttemptDto): Result<QuizAttemptDto> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.QUIZ_ATTEMPTS) {
                setBody(dto)
            }
        }
    }

    suspend fun getMyAttempts(userId: String, quizId: String): Result<List<QuizAttemptDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.QUIZ_ATTEMPTS) {
                parameter("user_id", "eq.$userId")
                parameter("quiz_id", "eq.$quizId")
                parameter("order", "taken_at.desc")
            }
        }
    }
}
