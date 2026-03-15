package com.eduplatform.android.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.eduplatform.android.R
import com.eduplatform.android.navigation.Screen
import com.eduplatform.presentation.viewmodel.CourseIntent
import com.eduplatform.presentation.viewmodel.CourseViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(navController: NavHostController) {
    val vm: CourseViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.onIntent(CourseIntent.Load)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. HERO BANNER (Uçtan uca uzanır)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) 
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_landing_hero),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Karartma Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )
            }

            // Yumuşak Geçiş
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .offset(y = (-15).dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.White)
                        )
                    )
            )

            // 2. Logo ve Yazı + Sağ Tarafta Kişi (Login) İkonu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF3B82F6),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("EduPlatform", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                }

                IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person, 
                                contentDescription = "Giriş Yap", 
                                tint = Color(0xFF64748B), 
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Kategoriler
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kategoriler", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text("Hepsini Gör", color = Color(0xFF3B82F6), fontSize = 14.sp)
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val categories = listOf("Yazılım", "Tasarım", "Pazarlama", "Finans")
                items(categories) { cat ->
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp).clickable { navController.navigate(Screen.Login.route) }
                    ) {
                        Text(cat, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // 4. Eğitimler (2'li Grid)
            Text(
                "Popüler Eğitimler", 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(start = 20.dp, top = 32.dp, bottom = 16.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                state.filteredCourses.chunked(2).forEach { rowCourses ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowCourses.forEach { course ->
                            Box(modifier = Modifier.weight(1f)) {
                                LandingCourseGridItem(course) { navController.navigate(Screen.Login.route) }
                            }
                        }
                        if (rowCourses.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun LandingCourseGridItem(course: com.eduplatform.domain.model.Course, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(course.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ücretsiz", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
