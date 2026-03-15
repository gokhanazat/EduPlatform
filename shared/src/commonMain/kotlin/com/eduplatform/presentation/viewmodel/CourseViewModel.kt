package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.Course
import com.eduplatform.domain.model.Enrollment
import com.eduplatform.domain.repository.CourseRepository
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess

data class CourseState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val courses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val enrolledCourseIds: Set<String> = emptySet(),
    val enrollmentMap: Map<String, Enrollment> = emptyMap(), // courseId -> Enrollment
    val enrollingCourseId: String? = null
)

sealed class CourseIntent {
    object Load : CourseIntent()
    data class LoadUserEntries(val userId: String) : CourseIntent()
    data class FilterCategory(val cat: String?) : CourseIntent()
    data class Search(val query: String) : CourseIntent()
    data class Enroll(val userId: String, val courseId: String) : CourseIntent()
    object ClearFilters : CourseIntent()
    object ClearError : CourseIntent()
}

class CourseViewModel(private val repository: CourseRepository) : BaseViewModel<CourseState, CourseIntent>(CourseState()) {

    override suspend fun handleIntent(intent: CourseIntent) {
        when (intent) {
            is CourseIntent.Load -> {
                setState { copy(isLoading = true, error = null) }
                repository.getAllCourses().onSuccess { list ->
                    setState { copy(courses = list, filteredCourses = applyFilters(list, selectedCategory, searchQuery)) }
                    repository.getCategories().onSuccess { cats -> setState { copy(categories = cats) } }
                }.onError { message ->
                    setState { copy(error = message) }
                }
                setState { copy(isLoading = false) }
            }
            is CourseIntent.LoadUserEntries -> {
                repository.getEnrolledCourses(intent.userId).onSuccess { list ->
                    val ids = list.map { it.courseId }.toSet()
                    val map = list.associateBy { it.courseId }
                    setState { copy(enrolledCourseIds = ids, enrollmentMap = map) }
                }
            }
            is CourseIntent.FilterCategory -> {
                setState { copy(selectedCategory = intent.cat, filteredCourses = applyFilters(courses, intent.cat, searchQuery)) }
            }
            is CourseIntent.Search -> {
                setState { copy(searchQuery = intent.query, filteredCourses = applyFilters(courses, selectedCategory, intent.query)) }
            }
            is CourseIntent.Enroll -> {
                setState { copy(enrollingCourseId = intent.courseId) }
                repository.enrollInCourse(intent.userId, intent.courseId)
                    .onSuccess { enrollment ->
                        setState { copy(
                            enrolledCourseIds = enrolledCourseIds + intent.courseId,
                            enrollmentMap = enrollmentMap + (intent.courseId to enrollment),
                            enrollingCourseId = null
                        ) }
                    }
                    .onError { message ->
                        setState { copy(error = message, enrollingCourseId = null) }
                    }
            }
            is CourseIntent.ClearFilters -> {
                setState { copy(selectedCategory = null, searchQuery = "", filteredCourses = courses) }
            }
            is CourseIntent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun applyFilters(list: List<Course>, cat: String?, query: String): List<Course> {
        return list.filter { course ->
            val matchCat = cat == null || course.category == cat
            val matchQuery = query.isEmpty() || course.title.contains(query, ignoreCase = true) || course.description.contains(query, ignoreCase = true)
            matchCat && matchQuery
        }
    }
}
