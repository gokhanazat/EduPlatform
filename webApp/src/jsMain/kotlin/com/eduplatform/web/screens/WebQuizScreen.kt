package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.domain.model.QuestionType
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.QuizIntent
import com.eduplatform.presentation.viewmodel.QuizViewModel
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebQuizScreen(courseId: String, onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val vm: QuizViewModel by koin.inject()

    val authState by authVM.state.collectAsState()
    val state by vm.state.collectAsState()
    val userId = authState.currentUser?.id ?: ""

    LaunchedEffect(courseId, userId) {
        if (userId.isNotEmpty()) {
            vm.onIntent(QuizIntent.Load(userId, courseId))
        }
    }

    // Navigation on submit
    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted && state.attempt != null) {
            // Web routing would normally handle this, simplified here
            onNav("/home") 
        }
    }

    if (state.isLoading) {
        Div({ style { textAlign("center"); padding(100.px) } }) { Text("Sınav yükleniyor...") }
        return
    }

    val quiz = state.quiz ?: return
    val question = quiz.questions.getOrNull(state.currentIndex) ?: return

    Div({
        style {
            maxWidth(800.px)
            property("margin", "0 auto")
        }
    }) {
        // Progress & Timer
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                marginBottom(12.px)
                alignItems(AlignItems.Center)
            }
        }) {
            H3 { Text("Soru ${state.currentIndex + 1} / ${quiz.questions.size}") }
            state.timeRemaining?.let { sec ->
                Div({
                    style {
                        color(if (sec < 60) Color("red") else Color("#4F46E5"))
                        fontWeight(700)
                        fontSize(20.px)
                    }
                }) {
                    val m = sec / 60
                    val s = sec % 60
                    Text("${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}")
                }
            }
        }

        // Progress Bar
        Div({
            style {
                width(100.percent)
                height(8.px)
                backgroundColor(Color("#E2E8F0"))
                borderRadius(4.px)
                marginBottom(32.px)
                overflow("hidden")
            }
        }) {
            Div({
                style {
                    width(((state.currentIndex + 1).toFloat() / quiz.questions.size * 100).percent)
                    height(100.percent)
                    backgroundColor(Color("#4F46E5"))
                }
            }) {}
        }

        // Question Card
        Div({
            style {
                backgroundColor(Color.white)
                padding(40.px)
                borderRadius(16.px)
                property("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
                marginBottom(24.px)
            }
        }) {
            H2({ style { property("line-height", "1.4") } }) { Text(question.questionText) }
        }

        // Options
        Div {
            question.options.forEachIndexed { index, option ->
                val isSelected = state.answers[question.id] == option.id
                val labels = if (question.type == QuestionType.MULTIPLE_CHOICE) 
                    listOf("A", "B", "C", "D", "E") else listOf("Doğru", "Yanlış")

                Div({
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        padding(16.px)
                        backgroundColor(if (isSelected) Color("#EEF2FF") else Color.white)
                        border(2.px, LineStyle.Solid, if (isSelected) Color("#4F46E5") else Color("#E2E8F0"))
                        borderRadius(12.px)
                        marginBottom(12.px)
                        cursor("pointer")
                        property("transition", "all 0.2s")
                    }
                    onClick { vm.onIntent(QuizIntent.SelectAnswer(question.id, option.id)) }
                }) {
                    Div({
                        style {
                            width(32.px)
                            height(32.px)
                            borderRadius(6.px)
                            backgroundColor(if (isSelected) Color("#4F46E5") else Color("#F1F5F9"))
                            color(if (isSelected) Color.white else Color("#64748B"))
                            display(DisplayStyle.Flex)
                            alignItems(AlignItems.Center)
                            justifyContent(JustifyContent.Center)
                            fontWeight(700)
                            marginRight(16.px)
                        }
                    }) { Text(labels.getOrElse(index) { "" }) }
                    
                    Text(option.optionText)
                }
            }
        }

        // Navigation
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.FlexEnd)
                marginTop(32.px)
            }
        }) {
            val isLast = state.currentIndex == quiz.questions.size - 1
            val hasAnswered = state.answers.containsKey(question.id)

            Button(attrs = {
                style {
                    padding(12.px, 40.px)
                    backgroundColor(if (hasAnswered) Color("#4F46E5") else Color("#CBD5E1"))
                    color(Color.white)
                    border(0.px)
                    borderRadius(8.px)
                    fontWeight(600)
                    cursor(if (hasAnswered) "pointer" else "default")
                }
                if (!hasAnswered) attr("disabled", "true")
                onClick {
                    if (isLast) vm.onIntent(QuizIntent.Submit(userId))
                    else vm.onIntent(QuizIntent.Next)
                }
            }) {
                if (state.isSubmitting) Text("Gönderiliyor...")
                else if (isLast) Text("Sınavı Bitir")
                else Text("Sonraki Soru →")
            }
        }
    }
}
