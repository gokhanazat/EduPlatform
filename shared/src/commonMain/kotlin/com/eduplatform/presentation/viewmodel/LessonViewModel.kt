package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.Enrollment
import com.eduplatform.domain.model.Lesson
import com.eduplatform.domain.repository.LessonRepository
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess

data class LessonState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lessons: List<Lesson> = emptyList(),
    val currentLesson: Lesson? = null,
    val enrollment: Enrollment? = null,
    val completedLessonIds: Set<String> = emptySet(),
    val markingComplete: Boolean = false
)

sealed class LessonIntent {
    data class Load(val userId: String, val courseId: String) : LessonIntent()
    data class Select(val lesson: Lesson) : LessonIntent()
    data class MarkComplete(val userId: String, val lessonId: String, val courseId: String) : LessonIntent()
    object SelectNext : LessonIntent()
    object SelectPrev : LessonIntent()
    object ClearError : LessonIntent()
}

class LessonViewModel(private val repository: LessonRepository) : BaseViewModel<LessonState, LessonIntent>(LessonState()) {

    override suspend fun handleIntent(intent: LessonIntent) {
        when (intent) {
            is LessonIntent.Load -> {
                setState { copy(isLoading = true, error = null) }
                repository.getLessonsForCourse(intent.courseId).onSuccess { list ->
                    setState { copy(lessons = list, currentLesson = list.firstOrNull()) }
                    repository.getProgress(intent.userId, intent.courseId).onSuccess { progress ->
                        setState { copy(enrollment = progress) }
                    }
                }.onError { message ->
                    setState { copy(error = message) }
                }
                setState { copy(isLoading = false) }
            }
            is LessonIntent.Select -> {
                setState { copy(currentLesson = intent.lesson) }
            }
            is LessonIntent.MarkComplete -> {
                setState { copy(markingComplete = true) }
                repository.markLessonComplete(intent.userId, intent.lessonId, intent.courseId)
                    .onSuccess {
                        setState { copy(markingComplete = false, completedLessonIds = completedLessonIds + intent.lessonId) }
                    }
                    .onError { message ->
                        setState { copy(markingComplete = false, error = message) }
                    }
            }
            is LessonIntent.SelectNext -> {
                val currentIndex = state.value.lessons.indexOf(state.value.currentLesson)
                if (currentIndex < state.value.lessons.size - 1) {
                    setState { copy(currentLesson = state.value.lessons[currentIndex + 1]) }
                }
            }
            is LessonIntent.SelectPrev -> {
                val currentIndex = state.value.lessons.indexOf(state.value.currentLesson)
                if (currentIndex > 0) {
                    setState { copy(currentLesson = state.value.lessons[currentIndex - 1]) }
                }
            }
            is LessonIntent.ClearError -> setState { copy(error = null) }
        }
    }
}
