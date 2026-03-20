package com.eduplatform.data.api

import com.eduplatform.data.api.dto.EnrollmentDto
import com.eduplatform.data.api.dto.LessonDto
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class LessonApiService(private val client: HttpClient) {

    suspend fun getLessons(courseId: String): Result<List<LessonDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.LESSONS) {
                parameter("course_id", "eq.$courseId")
                parameter("order", "order_index")
            }
        }
    }

    suspend fun markComplete(userId: String, lessonId: String): Result<Unit> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.LESSON_PROGRESS) {
                header("Prefer", "resolution=merge-duplicates")
                setBody(mapOf("profile_id" to userId, "lesson_id" to lessonId))
            }
        }
    }

    suspend fun getProgress(userId: String, courseId: String): Result<EnrollmentDto?> {
        return handleApiCall<List<EnrollmentDto>> {
            client.get(ApiConfig.BASE_URL + ApiConfig.ENROLLMENTS) {
                parameter("profile_id", "eq.$userId")
                parameter("course_id", "eq.$courseId")
            }
        }.let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.firstOrNull())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }
}
