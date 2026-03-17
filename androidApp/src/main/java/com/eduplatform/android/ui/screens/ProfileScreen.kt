package com.eduplatform.android.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.eduplatform.android.navigation.Screen
import com.eduplatform.presentation.viewmodel.*
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val authVM: AuthViewModel = koinInject()
    val courseVM: CourseViewModel = koinInject()
    val certVM: CertViewModel = koinInject()

    val authState by authVM.state.collectAsStateWithLifecycle()
    val courseState by courseVM.state.collectAsStateWithLifecycle()
    val certState by certVM.state.collectAsStateWithLifecycle()

    val user = authState.currentUser
    val enrolledCourses = courseState.courses.filter { it.id in courseState.enrolledCourseIds }

    LaunchedEffect(user?.id) {
        user?.id?.let { certVM.onIntent(CertIntent.Load(it)) }
    }

    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn) {
            navController.navigate(Screen.Landing.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E293B)) },
                actions = {
                    IconButton(onClick = { authVM.onIntent(AuthIntent.SignOut) }) {
                        Icon(Icons.Default.Logout, contentDescription = "Çıkış Yap", tint = Color(0xFFEF4444))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Profil Kartı
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .border(3.dp, Color(0xFFEFF6FF), CircleShape),
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9)
                    ) {
                        AsyncImage(
                            model = "https://ui-avatars.com/api/?name=${user?.fullName ?: "User"}&background=3B82F6&color=fff",
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(user?.fullName ?: "Kullanıcı", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF1E293B))
                Text(user?.sicilNo ?: "Sicil No: ---", fontSize = 14.sp, color = Color(0xFF64748B))
            }

            // 2. İstatistikler
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatBox(
                    label = "Eğitimler",
                    value = courseState.enrolledCourseIds.size.toString(),
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatBox(
                    label = "Sertifikalar",
                    value = certState.certificates.size.toString(),
                    icon = Icons.Default.Verified,
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Eğitimlerim (2'li Grid)
            Text(
                "Eğitimlerim", 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp, 
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(start = 20.dp, top = 32.dp, bottom = 16.dp)
            )

            if (enrolledCourses.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("Henüz bir eğitime katılmadınız.", color = Color(0xFF94A3B8))
                }
            } else {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    enrolledCourses.chunked(2).forEach { rowCourses ->
                        Row(
                            modifier = Modifier.fillMaxWidth(), 
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowCourses.forEach { course ->
                                Box(modifier = Modifier.weight(1f)) {
                                    // HomeScreen'deki CourseGridItem ile aynı tasarım
                                    ProfileCourseGridItem(course) { 
                                        navController.navigate(Screen.CourseDetail.go(course.id)) 
                                    }
                                }
                            }
                            if (rowCourses.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileStatBox(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF1E293B))
            Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun ProfileCourseGridItem(course: com.eduplatform.domain.model.Course, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            course.title, 
            fontWeight = FontWeight.Bold, 
            fontSize = 13.sp, 
            maxLines = 1,
            color = Color(0xFF1E293B)
        )
        
        // Profil için ilerleme çubuğu ekleyelim
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = 0.7f, // Örnek ilerleme
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = Color(0xFF3B82F6),
            trackColor = Color(0xFFE2E8F0)
        )
    }
}
