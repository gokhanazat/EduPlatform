package com.eduplatform.android.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.theme.Primary
import com.eduplatform.presentation.viewmodel.AuthIntent
import com.eduplatform.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun SplashScreen(navController: NavHostController) {
    val vm: AuthViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()

    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        vm.onIntent(AuthIntent.CheckAuth)
    }

    LaunchedEffect(state.isLoggedIn, state.successEvent) {
        if (state.successEvent || state.isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
            vm.onIntent(AuthIntent.ClearEvent)
        }
    }

    LaunchedEffect(Unit) {
        delay(1500)
        if (!vm.state.value.isLoggedIn) {
            navController.navigate(Screen.Landing.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha).scale(scale)
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "EduPlatform",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
