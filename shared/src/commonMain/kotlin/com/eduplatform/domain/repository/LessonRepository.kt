package com.eduplatform.domain.repository

import com.eduplatform.domain.model.Enrollment
import com.eduplatform.domain.model.Lesson
import com.eduplatform.util.Result

interface LessonRepository {
    suspend fun getLessonsForCourse(courseId: String): Result<List<Lesson>>
    suspend fun markLessonComplete(userId: String, lessonId: String, courseId: String): Result<Unit>
    suspend fun getProgress(userId: String, courseId: String): Result<Enrollment?>
}
