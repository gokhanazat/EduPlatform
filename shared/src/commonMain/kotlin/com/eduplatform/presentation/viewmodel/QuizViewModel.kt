package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.Quiz
import com.eduplatform.domain.model.QuizAttempt
import com.eduplatform.domain.repository.QuizRepository
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class QuizState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val quiz: Quiz? = null,
    val currentIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(),
    val timeRemaining: Int? = null,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val attempt: QuizAttempt? = null,
    val previousAttempts: List<QuizAttempt> = emptyList()
)

sealed class QuizIntent {
    data class Load(val userId: String, val courseId: String) : QuizIntent()
    data class SelectAnswer(val questionId: String, val optionId: String) : QuizIntent()
    object Next : QuizIntent()
    object Prev : QuizIntent()
    data class Submit(val userId: String) : QuizIntent()
    object Restart : QuizIntent()
    object TimerTick : QuizIntent()
    object ClearError : QuizIntent()
}

class QuizViewModel(private val repository: QuizRepository) : BaseViewModel<QuizState, QuizIntent>(QuizState()) {

    override suspend fun handleIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.Load -> {
                setState { copy(isLoading = true, error = null) }
                repository.getQuizForCourse(intent.courseId).onSuccess { quizData ->
                    setState { 
                        copy(
                            quiz = quizData, 
                            timeRemaining = quizData.timeLimitMinutes?.let { it * 60 },
                            currentIndex = 0,
                            answers = emptyMap(),
                            isSubmitted = false
                        ) 
                    }
                    startTimer()
                    repository.getMyAttempts(intent.userId, quizData.id).onSuccess { attempts ->
                        setState { copy(previousAttempts = attempts) }
                    }
                }.onError { message ->
                    setState { copy(error = message) }
                }
                setState { copy(isLoading = false) }
            }
            is QuizIntent.SelectAnswer -> {
                val newAnswers = state.value.answers.toMutableMap()
                newAnswers[intent.questionId] = intent.optionId
                setState { copy(answers = newAnswers) }
            }
            is QuizIntent.Next -> {
                val quiz = state.value.quiz ?: return
                if (state.value.currentIndex < quiz.questions.size - 1) {
                    setState { copy(currentIndex = currentIndex + 1) }
                }
            }
            is QuizIntent.Prev -> {
                if (state.value.currentIndex > 0) {
                    setState { copy(currentIndex = currentIndex - 1) }
                }
            }
            is QuizIntent.Submit -> {
                val quiz = state.value.quiz ?: return
                setState { copy(isSubmitting = true) }
                
                val correctCount = quiz.questions.count { q ->
                    val selectedOptionId = state.value.answers[q.id]
                    q.options.any { it.id == selectedOptionId && it.isCorrect }
                }
                val score = if (quiz.questions.isEmpty()) 0 else (correctCount * 100) / quiz.questions.size
                val passed = score >= quiz.passScorePercent
                
                val attemptRequest = QuizAttempt(
                    userId = intent.userId,
                    quizId = quiz.id,
                    score = score,
                    passed = passed,
                    answers = state.value.answers
                )
                
                repository.submitAttempt(attemptRequest)
                    .onSuccess { savedAttempt ->
                        setState { copy(isSubmitting = false, isSubmitted = true, attempt = savedAttempt) }
                    }
                    .onError { message ->
                        setState { copy(isSubmitting = false, error = message) }
                    }
            }
            is QuizIntent.Restart -> {
                setState { copy(isSubmitted = false, currentIndex = 0, answers = emptyMap(), attempt = null) }
                startTimer()
            }
            is QuizIntent.TimerTick -> {
                val current = state.value.timeRemaining ?: return
                if (current > 0) {
                    setState { copy(timeRemaining = current - 1) }
                } else {
                    // Could auto-submit here if time is 0
                }
            }
            is QuizIntent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (state.value.timeRemaining != null && state.value.timeRemaining!! > 0 && !state.value.isSubmitted) {
                delay(1000)
                handleIntent(QuizIntent.TimerTick)
            }
        }
    }
}
