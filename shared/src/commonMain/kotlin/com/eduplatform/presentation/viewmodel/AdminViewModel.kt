package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.Course
import com.eduplatform.domain.repository.AuthRepository
import com.eduplatform.domain.repository.CourseRepository
import com.eduplatform.util.Result
import com.eduplatform.util.onSuccess
import com.eduplatform.util.onError
import com.eduplatform.data.api.dto.WhitelistDto

data class AdminState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val whitelist: List<WhitelistDto> = emptyList(),
    val courses: List<Course> = emptyList(),
    val successMessage: String? = null,
    val whitelistSearch: String = "",
    // Dashboard Stats
    val totalStudents: Int = 0,
    val publishedCoursesCount: Int = 0,
    val totalEnrollments: Int = 0,
    val totalCertificates: Int = 0
)

sealed class AdminIntent {
    data class LoadWhitelist(val search: String? = null) : AdminIntent()
    data class AddToWhitelist(val email: String?, val sicilNo: String?, val city: String, val notes: String = "") : AdminIntent()
    data class UpdateWhitelist(val id: String, val updates: Map<String, Any>) : AdminIntent()
    data class RemoveFromWhitelist(val id: String) : AdminIntent()
    data class ImportWhitelistCsv(val csvData: String) : AdminIntent()
    
    object LoadCourses : AdminIntent()
    data class CreateCourse(val course: Course) : AdminIntent()
    data class UpdateCourse(val courseId: String, val updates: Map<String, Any>) : AdminIntent()
    data class DeleteCourse(val courseId: String) : AdminIntent()
    
    object LoadDashboardStats : AdminIntent()
    
    object ClearMessages : AdminIntent()
}

class AdminViewModel(
    private val authRepository: AuthRepository,
    private val courseRepository: CourseRepository
) : BaseViewModel<AdminState, AdminIntent>(AdminState()) {

    override suspend fun handleIntent(intent: AdminIntent) {
        when (intent) {
            is AdminIntent.LoadWhitelist -> {
                setState { copy(isLoading = true, whitelistSearch = intent.search ?: "") }
                authRepository.getWhitelist(intent.search).onSuccess { list ->
                    setState { copy(isLoading = false, whitelist = list) }
                }.onError { msg ->
                    setState { copy(isLoading = false, error = msg) }
                }
            }
            is AdminIntent.AddToWhitelist -> {
                setState { copy(isLoading = true) }
                authRepository.addToWhitelist(intent.email, intent.sicilNo, intent.city, intent.notes).onSuccess {
                    handleIntent(AdminIntent.LoadWhitelist(state.value.whitelistSearch))
                    setState { copy(successMessage = "Kullanıcı başarıyla eklendi", isLoading = false) }
                }.onError { msg ->
                    setState { copy(isLoading = false, error = msg) }
                }
            }
            is AdminIntent.UpdateWhitelist -> {
                authRepository.updateWhitelist(intent.id, intent.updates).onSuccess {
                    handleIntent(AdminIntent.LoadWhitelist(state.value.whitelistSearch))
                }
            }
            is AdminIntent.RemoveFromWhitelist -> {
                authRepository.removeFromWhitelist(intent.id).onSuccess {
                    handleIntent(AdminIntent.LoadWhitelist(state.value.whitelistSearch))
                }
            }
            is AdminIntent.ImportWhitelistCsv -> {
                setState { copy(isLoading = true) }
                val rows = intent.csvData.split("\n").drop(1).filter { it.isNotBlank() }
                var successCount = 0
                rows.forEach { row ->
                    val cols = row.split(",").map { it.trim().replace("\"", "") }
                    if (cols.size >= 2) {
                        val email = cols[0]
                        val city = cols[1]
                        authRepository.addToWhitelist(email, null, city).onSuccess { successCount++ }
                    }
                }
                handleIntent(AdminIntent.LoadWhitelist())
                setState { copy(isLoading = false, successMessage = "$successCount kullanıcı başarıyla yüklendi.") }
            }
            AdminIntent.LoadCourses -> {
                setState { copy(isLoading = true) }
                courseRepository.getAllCourses().onSuccess { list ->
                    setState { copy(isLoading = false, courses = list) }
                }.onError { msg ->
                    setState { copy(isLoading = false, error = msg) }
                }
            }
            is AdminIntent.UpdateCourse -> {
                courseRepository.updateCourse(intent.courseId, intent.updates).onSuccess {
                    handleIntent(AdminIntent.LoadCourses)
                }
            }
            is AdminIntent.DeleteCourse -> {
                courseRepository.deleteCourse(intent.courseId).onSuccess {
                    handleIntent(AdminIntent.LoadCourses)
                }
            }
            is AdminIntent.CreateCourse -> {
                setState { copy(isLoading = true) }
                courseRepository.createCourse(intent.course).onSuccess {
                    handleIntent(AdminIntent.LoadCourses)
                    setState { copy(successMessage = "Kurs başarıyla oluşturuldu", isLoading = false) }
                }.onError { msg ->
                    setState { copy(isLoading = false, error = msg) }
                }
            }
            AdminIntent.LoadDashboardStats -> {
                setState { copy(isLoading = true) }
                val usersResult = authRepository.getWhitelist()
                val coursesResult = courseRepository.getAllCourses()
                
                var totalUsers = 0
                var pubCourses = 0
                var errorMsg: String? = null

                if (usersResult is Result.Success) {
                    totalUsers = usersResult.data.size
                } else if (usersResult is Result.Error) {
                    errorMsg = usersResult.message
                }

                if (coursesResult is Result.Success) {
                    pubCourses = coursesResult.data.count { it.isPublished }
                }

                setState { copy(
                    isLoading = false,
                    totalStudents = totalUsers,
                    publishedCoursesCount = pubCourses,
                    error = if (state.value.error == null) errorMsg else state.value.error
                )}
            }
            AdminIntent.ClearMessages -> {
                setState { copy(error = null, successMessage = null) }
            }
        }
    }
}
