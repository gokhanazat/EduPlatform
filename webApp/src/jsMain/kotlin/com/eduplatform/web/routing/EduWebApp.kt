package com.eduplatform.web.routing

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.*
import com.eduplatform.web.layout.WebScaffold
import com.eduplatform.web.screens.*
import kotlinx.browser.window
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun EduWebApp() {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val authState by authVM.state.collectAsState()
    
    var route by remember { mutableStateOf(window.location.hash.removePrefix("#").ifEmpty { "/" }) }

    LaunchedEffect(Unit) {
        window.addEventListener("hashchange", {
            route = window.location.hash.removePrefix("#").ifEmpty { "/" }
        })
    }

    fun nav(r: String) {
        window.location.hash = r
        route = r
    }

    // Auth guard
    LaunchedEffect(authState.isLoggedIn, route) {
        val publicRoutes = listOf("/", "/login", "/register", "/course/")
        if (!authState.isLoggedIn && !publicRoutes.any { route.startsWith(it) }) {
            nav("/login")
        }
        // Admin guard
        if (authState.isLoggedIn && route == "/admin" && authState.currentUser?.role != "admin") {
            nav("/home")
        }
        // Logged in users don't MUST be in /home anymore, they can browse vitrin (/)
        if (authState.isLoggedIn && (route == "/login" || route == "/register")) {
            nav("/home")
        }
    }

    WebScaffold(route = route, onNav = { nav(it) }, isLoggedIn = authState.isLoggedIn) {
        when {
            route == "/home" -> WebHomeScreen(onNav = { nav(it) })
            route == "/profile" -> WebProfileScreen(onNav = { nav(it) })
            route == "/admin" -> WebAdminScreen(onNav = { nav(it) })
            route == "/certificates" -> WebCertScreen(onNav = { nav(it) })
            route == "/" -> WebLandingScreen(onNav = { nav(it) })
            route == "/login" -> WebLoginScreen(onNav = { nav(it) })
            route == "/register" -> WebRegisterScreen(onNav = { nav(it) })
            route.startsWith("/course/") -> WebCourseDetailScreen(
                courseId = route.removePrefix("/course/"),
                onNav = { nav(it) }
            )
            route.startsWith("/lesson/") -> {
                val parts = route.removePrefix("/lesson/").split("/")
                WebLessonScreen(
                    lessonId = parts.getOrElse(0) { "" },
                    courseId = parts.getOrElse(1) { "" },
                    onNav = { nav(it) }
                )
            }
            route.startsWith("/quiz/") -> WebQuizScreen(
                courseId = route.removePrefix("/quiz/"),
                onNav = { nav(it) }
            )
            route.startsWith("/verify/") -> WebVerifyScreen(
                code = route.removePrefix("/verify/")
            )
            else -> WebHomeScreen(onNav = { nav(it) })
        }
    }
}
