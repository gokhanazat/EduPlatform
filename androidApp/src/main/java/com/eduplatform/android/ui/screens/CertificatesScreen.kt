package com.eduplatform.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.BottomNavBar
import com.eduplatform.android.ui.components.EmptyState
import com.eduplatform.android.ui.components.LoadingScreen
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.CertIntent
import com.eduplatform.presentation.viewmodel.CertViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificatesScreen(navController: NavHostController) {
    val authVM: AuthViewModel = koinInject()
    val authState by authVM.state.collectAsStateWithLifecycle()
    val userId = authState.currentUser?.id ?: ""

    val vm: CertViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            vm.onIntent(CertIntent.Load(userId))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sertifikalarım") })
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen()
            state.certificates.isEmpty() -> {
                EmptyState(
                    title = "Henüz sertifikanız yok",
                    subtitle = "Bir eğitimi tamamlayıp sınavı geçerek ilk sertifikanızı kazanın."
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(state.certificates) { cert ->
                        Card(
                            onClick = {
                                vm.onIntent(CertIntent.Select(cert))
                                navController.navigate(Screen.CertDetail.go(cert.id))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color(0xFFFDE68A).copy(alpha = 0.3f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cert.courseTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Veriliş Tarihi: ${cert.issuedAt.take(10)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
