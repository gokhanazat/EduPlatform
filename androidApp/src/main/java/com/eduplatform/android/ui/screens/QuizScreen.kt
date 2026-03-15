package com.eduplatform.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.EduButton
import com.eduplatform.android.ui.components.ErrorView
import com.eduplatform.android.ui.components.LoadingScreen
import com.eduplatform.domain.model.QuestionType
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.QuizIntent
import com.eduplatform.presentation.viewmodel.QuizViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(courseId: String, navController: NavHostController) {
    val authVM: AuthViewModel = koinInject()
    val authState by authVM.state.collectAsStateWithLifecycle()
    val userId = authState.currentUser?.id ?: ""

    val vm: QuizViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(courseId, userId) {
        if (userId.isNotEmpty()) {
            vm.onIntent(QuizIntent.Load(userId, courseId))
        }
    }

    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted && state.attempt != null) {
            navController.navigate(Screen.QuizResult.go(courseId, state.attempt!!.id)) {
                popUpTo(Screen.Quiz.route) { inclusive = true }
            }
        }
    }

    var showQuitDialog by remember { mutableStateOf(false) }
    BackHandler { showQuitDialog = true }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Sınavdan Çık?") },
            text = { Text("İlerlemeniz kaydedilmeyecek. Çıkmak istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Evet, Çık")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Hayır, Devam Et")
                }
            }
        )
    }

    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorView(state.error!!, onRetry = { 
            if (userId.isNotEmpty()) vm.onIntent(QuizIntent.Load(userId, courseId)) 
        })
        state.quiz != null -> {
            val quiz = state.quiz!!
            val question = quiz.questions.getOrNull(state.currentIndex) ?: return

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Soru ${state.currentIndex + 1} / ${quiz.questions.size}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                state.timeRemaining?.let { sec ->
                                    Text(
                                        text = formatTime(sec),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (sec < 30) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { showQuitDialog = true }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    LinearProgressIndicator(
                        progress = (state.currentIndex + 1).toFloat() / quiz.questions.size,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = question.questionText,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val labels = if (question.type == QuestionType.MULTIPLE_CHOICE) {
                        listOf("A", "B", "C", "D", "E")
                    } else {
                        listOf("Doğru", "Yanlış")
                    }

                    question.options.forEachIndexed { index, option ->
                        val isSelected = state.answers[question.id] == option.id
                        
                        OutlinedCard(
                            onClick = { vm.onIntent(QuizIntent.SelectAnswer(question.id, option.id)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = labels.getOrElse(index) { "" },
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Text(
                                    text = option.optionText,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    val isLast = state.currentIndex == quiz.questions.size - 1
                    val hasAnswered = state.answers.containsKey(question.id)

                    EduButton(
                        text = if (isLast) "Sınavı Bitir" else "Sonraki Soru →",
                        onClick = {
                            if (isLast) {
                                if (userId.isNotEmpty()) vm.onIntent(QuizIntent.Submit(userId))
                            } else {
                                vm.onIntent(QuizIntent.Next)
                            }
                        },
                        enabled = hasAnswered,
                        isLoading = state.isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
}
