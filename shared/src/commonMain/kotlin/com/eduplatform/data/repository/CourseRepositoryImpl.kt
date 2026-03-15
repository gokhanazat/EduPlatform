package com.eduplatform.data.repository

import com.eduplatform.data.api.CourseApiService
import com.eduplatform.data.api.dto.CourseDto
import com.eduplatform.db.EduDatabase
import com.eduplatform.domain.model.Course
import com.eduplatform.domain.model.Enrollment
import com.eduplatform.domain.repository.CourseRepository
import com.eduplatform.util.Result
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess

class CourseRepositoryImpl(
    private val api: CourseApiService,
    private val db: EduDatabase
) : CourseRepository {

    private val queries = db.eduDatabaseQueries

    override suspend fun getAllCourses(category: String?): Result<List<Course>> {
        val apiResult = api.getAllCourses(category)
        return when (apiResult) {
            is Result.Success -> {
                val domainList = apiResult.data.map { it.toDomain() }
                domainList.forEach { course ->
                    queries.insertCourse(
                        id = course.id,
                        title = course.title,
                        description = course.description,
                        category = course.category,
                        city = course.city,
                        instructorName = course.instructorName,
                        durationMinutes = course.durationMinutes,
                        hasCertificate = course.hasCertificate,
                        isPublished = course.isPublished,
                        thumbnailUrl = course.thumbnailUrl
                    )
                }
                Result.Success(domainList)
            }
            is Result.Error -> {
                val cached = queries.selectAllCourses().executeAsList().map { entity ->
                    Course(
                        id = entity.id,
                        title = entity.title,
                        description = entity.description,
                        category = entity.category,
                        city = entity.city ?: "",
                        instructorName = entity.instructorName,
                        durationMinutes = entity.durationMinutes,
                        hasCertificate = entity.hasCertificate,
                        isPublished = entity.isPublished,
                        thumbnailUrl = entity.thumbnailUrl
                    )
                }
                if (cached.isNotEmpty()) Result.Success(cached)
                else Result.Error(apiResult.message)
            }
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun getCourseById(id: String): Result<Course> {
        return api.getCourseById(id).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun getEnrolledCourses(userId: String): Result<List<Enrollment>> {
        return api.getEnrolledCourses(userId).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun enrollInCourse(userId: String, courseId: String): Result<Enrollment> {
        return api.enrollInCourse(userId, courseId).let { result ->
            when (result) {
                is Result.Success -> {
                    val enrollment = result.data.toDomain()
                    queries.insertEnrollment(
                        userId = userId,
                        courseId = courseId,
                        enrolledAt = enrollment.enrolledAt,
                        progressPercent = enrollment.progressPercent
                    )
                    Result.Success(enrollment)
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun getCategories(): Result<List<String>> = api.getCategories()

    override suspend fun createCourse(course: Course): Result<Course> {
        return api.createCourse(course.toDto()).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun updateCourse(id: String, updates: Map<String, Any>): Result<Unit> {
        return api.updateCourse(id, updates)
    }

    override suspend fun deleteCourse(id: String): Result<Unit> {
        return api.deleteCourse(id)
    }

    private fun Course.toDto() = CourseDto(
        id = if (id.isBlank()) null else id,
        title = title,
        description = description,
        category = category,
        city = city,
        instructor_name = instructorName,
        duration_minutes = durationMinutes,
        has_certificate = hasCertificate,
        is_published = isPublished,
        thumbnail_url = thumbnailUrl
    )
}
