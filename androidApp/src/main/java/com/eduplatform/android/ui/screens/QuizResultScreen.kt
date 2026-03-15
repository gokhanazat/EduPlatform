package com.eduplatform.android.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.EduButton
import com.eduplatform.presentation.viewmodel.CertIntent
import com.eduplatform.presentation.viewmodel.CertViewModel
import com.eduplatform.presentation.viewmodel.QuizIntent
import com.eduplatform.presentation.viewmodel.QuizViewModel
import org.koin.compose.koinInject

@Composable
fun QuizResultScreen(courseId: String, attemptId: String, navController: NavHostController) {
    val vm: QuizViewModel = koinInject()
    val certVM: CertViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()
    val attempt = state.attempt ?: return

    val animScore by animateFloatAsState(
        targetValue = attempt.score / 100f,
        animationSpec = tween(1000),
        label = "score"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(140.dp)) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = if (attempt.passed) Color(0xFF16A34A) else Color(0xFFDC2626),
                    startAngle = -90f,
                    sweepAngle = 360f * animScore,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Text(
                text = "${attempt.score}%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (attempt.passed) "Tebrikler, Geçtiniz! 🎉" else "Bu Sefer Olmadı",
            style = MaterialTheme.typography.titleLarge,
            color = if (attempt.passed) Color(0xFF16A34A) else MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (attempt.passed) "Eğitimi başarıyla tamamladınız." else "Sertifika alabilmek için %70 başarı sağlamanız gerekiyor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (attempt.passed) {
            EduButton(
                text = "Sertifikamı Gör",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    certVM.onIntent(CertIntent.Load(attempt.userId))
                    navController.navigate(Screen.Certificates.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        EduButton(
            text = if (attempt.passed) "Ana Sayfaya Dön" else "Tekrar Dene",
            outlined = true,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (attempt.passed) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    vm.onIntent(QuizIntent.Restart)
                    navController.popBackStack()
                }
            }
        )
    }
}
