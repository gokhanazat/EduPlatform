package com.eduplatform.android.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.*
import com.eduplatform.android.ui.screens.*

@Composable
fun EduNavGraph(navController: NavHostController, startDest: String) {
    NavHost(
        navController = navController,
        startDestination = startDest,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(250)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn(tween(250)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(200)) }
    ) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Landing.route) { LandingScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable(Screen.Certificates.route) { CertificatesScreen(navController) }
        composable(Screen.Admin.route) { AdminScreen(navController) } // YENİ: Admin ekranı eklendi
        
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailScreen(courseId, navController)
        }

        composable(
            route = Screen.LessonPlayer.route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            LessonPlayerScreen(lessonId, courseId, navController)
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            QuizScreen(courseId, navController)
        }

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType },
                navArgument("attemptId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val attemptId = backStackEntry.arguments?.getString("attemptId") ?: ""
            QuizResultScreen(courseId, attemptId, navController)
        }

        composable(
            route = Screen.CertDetail.route,
            arguments = listOf(navArgument("certId") { type = NavType.StringType })
        ) { backStackEntry ->
            val certId = backStackEntry.arguments?.getString("certId") ?: ""
            CertDetailScreen(certId, navController)
        }
    }
}
