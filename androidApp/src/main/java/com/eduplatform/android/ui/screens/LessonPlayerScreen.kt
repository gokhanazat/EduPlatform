package com.eduplatform.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.EduButton
import com.eduplatform.android.ui.components.VideoPlayer
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.LessonIntent
import com.eduplatform.presentation.viewmodel.LessonViewModel
import com.mikepenz.markdown.compose.Markdown
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonPlayerScreen(lessonId: String, courseId: String, navController: NavHostController) {
    val authVM: AuthViewModel = koinInject()
    val authState by authVM.state.collectAsStateWithLifecycle()
    val userId = authState.currentUser?.id ?: ""

    val vm: LessonViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId, userId) {
        if (state.lessons.isEmpty() && userId.isNotEmpty()) {
            vm.onIntent(LessonIntent.Load(userId, courseId))
        }
    }

    val lesson = state.lessons.find { it.id == lessonId } ?: state.currentLesson
    val lessons = state.lessons
    val currentIndex = lessons.indexOfFirst { it.id == lessonId }
    val isFirst = currentIndex <= 0
    val isLast = currentIndex >= lessons.size - 1 && lessons.isNotEmpty()
    val isDone = lessonId in state.completedLessonIds

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson?.title ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!isFirst) {
                                val prevId = lessons[currentIndex - 1].id
                                navController.navigate(Screen.LessonPlayer.go(prevId, courseId)) {
                                    popUpTo(Screen.LessonPlayer.route) { inclusive = true }
                                }
                            }
                        },
                        enabled = !isFirst
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Önceki")
                    }

                    EduButton(
                        text = when {
                            isDone && isLast -> "Sınava Geç →"
                            isDone -> "Tamamlandı ✓"
                            isLast -> "Tamamla & Sınava Geç"
                            else -> "Tamamlandı Olarak İşaretle"
                        },
                        onClick = {
                            if (!isDone && userId.isNotEmpty()) {
                                vm.onIntent(LessonIntent.MarkComplete(userId, lessonId, courseId))
                            }
                            if (isLast) {
                                navController.navigate(Screen.Quiz.go(courseId))
                            }
                        },
                        isLoading = state.markingComplete,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            if (!isLast) {
                                val nextId = lessons[currentIndex + 1].id
                                navController.navigate(Screen.LessonPlayer.go(nextId, courseId)) {
                                    popUpTo(Screen.LessonPlayer.route) { inclusive = true }
                                }
                            }
                        },
                        enabled = !isLast
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Sonraki")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (lesson?.contentType == "video" && !lesson.videoUrl.isNullOrEmpty()) {
                VideoPlayer(
                    url = lesson.videoUrl!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
            }

            if (!lesson?.contentMarkdown.isNullOrBlank()) {
                Markdown(
                    content = lesson!!.contentMarkdown!!,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
