package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.LessonIntent
import com.eduplatform.presentation.viewmodel.LessonViewModel
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebLessonScreen(lessonId: String, courseId: String, onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val vm: LessonViewModel by koin.inject()

    val authState by authVM.state.collectAsState()
    val state by vm.state.collectAsState()
    val userId = authState.currentUser?.id ?: ""

    LaunchedEffect(lessonId, userId) {
        if (state.lessons.isEmpty() && userId.isNotEmpty()) {
            vm.onIntent(LessonIntent.Load(userId, courseId))
        }
    }

    val lesson = state.lessons.find { it.id == lessonId }
    val currentIndex = state.lessons.indexOfFirst { it.id == lessonId }
    val isFirst = currentIndex <= 0
    val isLast = currentIndex >= state.lessons.size - 1
    val isDone = lessonId in state.completedLessonIds

    Div({
        style {
            maxWidth(900.px)
            property("margin", "0 auto")
            backgroundColor(Color.white)
            padding(40.px)
            borderRadius(16.px)
            property("box-shadow", "0 1px 3px 0 rgba(0, 0, 0, 0.1)")
        }
    }) {
        // Navigation / Header
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                marginBottom(32.px)
            }
        }) {
            Div({
                style { cursor("pointer"); color(Color("#64748B")) }
                onClick { onNav("/course/$courseId") }
            }) { Text("← Eğitime Dön") }
            
            Div({ style { fontWeight(600) } }) { 
                Text("Ders ${currentIndex + 1} / ${state.lessons.size}") 
            }
        }

        H1({ style { marginBottom(24.px) } }) { Text(lesson?.title ?: "") }

        // Video Player
        if (lesson?.contentType == "video" && !lesson.videoUrl.isNullOrEmpty()) {
            Div({
                style {
                    width(100.percent)
                    property("aspect-ratio", "16 / 9")
                    backgroundColor(Color.black)
                    borderRadius(12.px)
                    overflow("hidden")
                    marginBottom(32.px)
                }
            }) {
                Video(attrs = {
                    style { width(100.percent); height(100.percent) }
                    attr("controls", "true")
                }) {
                    Source(attrs = {
                        attr("src", lesson.videoUrl!!)
                        attr("type", "video/mp4")
                    })
                }
            }
        }

        // Markdown Content
        if (!lesson?.contentMarkdown.isNullOrBlank()) {
            Div({
                style {
                    property("line-height", "1.7")
                    color(Color("#334155"))
                    fontSize(17.px)
                }
                // In a real app, use a proper Markdown component or marked.js
                // Here we just display the raw text for simplicity
            }) {
                Text(lesson!!.contentMarkdown!!)
            }
        }

        Spacer()

        // Footer Navigation
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                marginTop(48.px)
                paddingTop(24.px)
                property("border-top", "1px solid #E2E8F0")
            }
        }) {
            Button(attrs = {
                style {
                    padding(10.px, 20.px)
                    backgroundColor(if (isFirst) Color("#F1F5F9") else Color.white)
                    border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    borderRadius(6.px)
                    cursor(if (isFirst) "default" else "pointer")
                    color(if (isFirst) Color("#94A3B8") else Color("#1E293B"))
                }
                if (isFirst) attr("disabled", "true")
                onClick {
                    val prevId = state.lessons[currentIndex - 1].id
                    onNav("/lesson/$prevId/$courseId")
                }
            }) { Text("← Önceki Ders") }

            Button({
                style {
                    padding(12.px, 24.px)
                    backgroundColor(if (isDone) Color("#F0FDF4") else Color("#4F46E5"))
                    color(if (isDone) Color("#16A34A") else Color.white)
                    border(0.px)
                    borderRadius(6.px)
                    fontWeight(600)
                    cursor("pointer")
                }
                onClick {
                    if (!isDone && userId.isNotEmpty()) {
                        vm.onIntent(LessonIntent.MarkComplete(userId, lessonId, courseId))
                    }
                    if (isLast) onNav("/quiz/$courseId")
                }
            }) {
                if (isDone && isLast) Text("Sınava Geç →")
                else if (isDone) Text("Tamamlandı ✓")
                else if (isLast) Text("Tamamla & Sınava Geç")
                else Text("Dersi Tamamla")
            }

            Button(attrs = {
                style {
                    padding(10.px, 20.px)
                    backgroundColor(if (isLast) Color("#F1F5F9") else Color.white)
                    border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    borderRadius(6.px)
                    cursor(if (isLast) "default" else "pointer")
                    color(if (isLast) Color("#94A3B8") else Color("#1E293B"))
                }
                if (isLast) attr("disabled", "true")
                onClick {
                    val nextId = state.lessons[currentIndex + 1].id
                    onNav("/lesson/$nextId/$courseId")
                }
            }) { Text("Sonraki Ders →") }
        }
    }
}

@Composable
fun Spacer() {
    Div({ style { height(40.px) } }) {}
}
