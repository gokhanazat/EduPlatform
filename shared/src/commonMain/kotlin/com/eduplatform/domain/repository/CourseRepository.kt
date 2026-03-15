package com.eduplatform.domain.repository

import com.eduplatform.domain.model.Course
import com.eduplatform.domain.model.Enrollment
import com.eduplatform.util.Result

interface CourseRepository {
    suspend fun getAllCourses(category: String? = null): Result<List<Course>>
    suspend fun getCourseById(id: String): Result<Course>
    suspend fun getEnrolledCourses(userId: String): Result<List<Enrollment>>
    suspend fun enrollInCourse(userId: String, courseId: String): Result<Enrollment>
    suspend fun getCategories(): Result<List<String>>

    // --- ADMIN İŞLEMLERİ (YENİ) ---
    suspend fun createCourse(course: Course): Result<Course>
    suspend fun updateCourse(id: String, updates: Map<String, Any>): Result<Unit>
    suspend fun deleteCourse(id: String): Result<Unit>
}
