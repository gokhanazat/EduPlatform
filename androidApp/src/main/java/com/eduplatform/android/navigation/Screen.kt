package com.eduplatform.android.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Admin : Screen("admin") // YENİ: Admin sayfası rotası
    object CourseDetail : Screen("course/{courseId}") {
        fun go(id: String) = "course/$id"
    }
    object LessonPlayer : Screen("lesson/{lessonId}/{courseId}") {
        fun go(lessonId: String, courseId: String) = "lesson/$lessonId/$courseId"
    }
    object Quiz : Screen("quiz/{courseId}") {
        fun go(id: String) = "quiz/$id"
    }
    object QuizResult : Screen("quiz_result/{courseId}/{attemptId}") {
        fun go(courseId: String, attemptId: String) = "quiz_result/$courseId/$attemptId"
    }
    object Certificates : Screen("certificates")
    object CertDetail : Screen("cert/{certId}") {
        fun go(id: String) = "cert/$id"
    }
}
