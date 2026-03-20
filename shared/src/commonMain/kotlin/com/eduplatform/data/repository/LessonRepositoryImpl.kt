package com.eduplatform.data.repository

import com.eduplatform.data.api.LessonApiService
import com.eduplatform.db.EduDatabase
import com.eduplatform.domain.model.Enrollment
import com.eduplatform.domain.model.Lesson
import com.eduplatform.domain.repository.LessonRepository
import com.eduplatform.util.Result
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess

class LessonRepositoryImpl(
    private val api: LessonApiService,
    private val db: EduDatabase
) : LessonRepository {

    private val queries = db.eduDatabaseQueries

    override suspend fun getLessonsForCourse(courseId: String): Result<List<Lesson>> {
        val apiResult = api.getLessons(courseId)
        return when (apiResult) {
            is Result.Success -> {
                val domainList = apiResult.data.map { it.toDomain() }
                queries.transaction {
                    queries.deleteLessonsForCourse(courseId)
                    domainList.forEach { lesson ->
                        queries.insertLesson(
                            id = lesson.id,
                            courseId = lesson.courseId,
                            title = lesson.title,
                            contentType = lesson.contentType,
                            contentMarkdown = lesson.contentMarkdown,
                            videoUrl = lesson.videoUrl,
                            orderIndex = lesson.orderIndex
                        )
                    }
                }
                Result.Success(domainList)
            }
            is Result.Error -> {
                val cached = queries.selectLessonsForCourse(courseId).executeAsList().map { entity ->
                    Lesson(
                        id = entity.id,
                        courseId = entity.courseId,
                        title = entity.title,
                        contentType = entity.contentType,
                        contentMarkdown = entity.contentMarkdown,
                        videoUrl = entity.videoUrl,
                        orderIndex = entity.orderIndex
                    )
                }
                if (cached.isNotEmpty()) Result.Success(cached)
                else Result.Error(apiResult.message)
            }
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun markLessonComplete(userId: String, lessonId: String, courseId: String): Result<Unit> {
        // Optimistic update could be handled in ViewModel or here via a separate "completed" table
        // For simplicity, we just call the API
        return api.markComplete(userId, lessonId)
    }

    override suspend fun getProgress(userId: String, courseId: String): Result<Enrollment?> {
        return api.getProgress(userId, courseId).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data?.toDomain())
                is Result.Error -> {
                    // Try local DB
                    val cached = queries.selectEnrollment(userId, courseId).executeAsOneOrNull()
                    if (cached != null) {
                        Result.Success(Enrollment(
                            userId = cached.userId,
                            courseId = cached.courseId,
                            enrolledAt = cached.enrolledAt,
                            progressPercent = cached.progressPercent
                        ))
                    } else result
                }
                is Result.Loading -> result
            }
        }
    }
}
