package com.eduplatform.data.api

import com.eduplatform.data.api.dto.CourseDto
import com.eduplatform.data.api.dto.EnrollmentDto
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class CourseApiService(private val client: HttpClient) {

    // --- ÖĞRENCİ FONKSİYONLARI ---
    suspend fun getAllCourses(category: String? = null): Result<List<CourseDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("is_published", "eq.true")
                category?.let { parameter("category", "eq.$it") }
            }
        }
    }

    suspend fun getCourseById(id: String): Result<CourseDto> {
        return handleApiCall<List<CourseDto>> {
            client.get(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("id", "eq.$id")
            }
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val course = result.data.firstOrNull()
                    if (course != null) Result.Success(course)
                    else Result.Error("İçerik bulunamadı.")
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    // --- ADMIN FONKSİYONLARI (YENİ) ---
    
    suspend fun getAdminCourses(): Result<List<CourseDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("order", "created_at.desc")
            }
        }
    }

    suspend fun createCourse(course: CourseDto): Result<CourseDto> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                setBody(course)
            }
        }
    }

    suspend fun updateCourse(id: String, updates: Map<String, Any>): Result<Unit> {
        return handleApiCall {
            client.patch(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("id", "eq.$id")
                setBody(updates)
            }
        }
    }

    suspend fun deleteCourse(id: String): Result<Unit> {
        return handleApiCall {
            client.delete(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("id", "eq.$id")
            }
        }
    }

    // --- DİĞERLERİ ---
    suspend fun getEnrolledCourses(userId: String): Result<List<EnrollmentDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.ENROLLMENTS) {
                parameter("user_id", "eq.$userId")
                parameter("select", "*,courses(*)")
            }
        }
    }

    suspend fun enrollInCourse(userId: String, courseId: String): Result<EnrollmentDto> {
        return handleApiCall {
            client.post(ApiConfig.BASE_URL + ApiConfig.ENROLLMENTS) {
                setBody(mapOf("user_id" to userId, "course_id" to courseId))
            }
        }
    }

    suspend fun getCategories(): Result<List<String>> {
        return handleApiCall<List<CourseDto>> {
            client.get(ApiConfig.BASE_URL + ApiConfig.COURSES) {
                parameter("select", "category")
                parameter("is_published", "eq.true")
            }
        }.let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.category }.distinct())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }
}
