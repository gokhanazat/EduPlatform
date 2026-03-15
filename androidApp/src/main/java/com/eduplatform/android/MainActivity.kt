package com.eduplatform.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eduplatform.android.navigation.EduNavGraph
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.BottomNavBar
import com.eduplatform.android.ui.theme.EduTheme
import com.eduplatform.presentation.viewmodel.AuthViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Sistem çubuklarının altına uzanmayı etkinleştir
        enableEdgeToEdge()
        
        setContent {
            EduTheme {
                val navController = rememberNavController()
                val authVM: AuthViewModel = koinInject()
                
                val authState by authVM.state.collectAsStateWithLifecycle()
                val isAdmin = authState.currentUser?.role == "admin"
                val isLoggedIn = authState.isLoggedIn
                
                val startDest = if (isLoggedIn) Screen.Home.route else Screen.Landing.route
                
                Scaffold(
                    // İçerik penceresi insets'lerini sıfırlıyoruz ki ekranlar tam boy çalışabilsin
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination?.route
                        
                        val topLevelScreens = listOf(
                            Screen.Home.route, 
                            Screen.Certificates.route, 
                            Screen.Profile.route, 
                            Screen.Admin.route
                        )

                        if (isLoggedIn && currentRoute in topLevelScreens) {
                            BottomNavBar(navController, currentRoute, isAdmin)
                        }
                    }
                ) { padding ->
                    // Sadece alt barın kapladığı alanı (bottom padding) koruyoruz.
                    // Üst kısım (status bar) artık ekranların kendi kontrolünde olacak.
                    Box(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) {
                        EduNavGraph(navController, startDest)
                    }
                }
            }
        }
    }
}
