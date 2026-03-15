package com.eduplatform.android.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.presentation.viewmodel.AdminIntent
import com.eduplatform.presentation.viewmodel.AdminViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController) {
    val vm: AdminViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Genel Bakış", "Whitelist", "Eğitimler")

    // Search & Dialog states
    var searchText by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var courseToEdit by remember { mutableStateOf<com.eduplatform.domain.model.Course?>(null) }

    // CSV Picker
    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val content = context.contentResolver.openInputStream(it)?.bufferedReader()?.use { it.readText() }
            content?.let { vm.onIntent(AdminIntent.ImportWhitelistCsv(it)) }
        }
    }

    LaunchedEffect(selectedTab, searchText) {
        when (selectedTab) {
            0 -> vm.onIntent(AdminIntent.LoadDashboardStats)
            1 -> vm.onIntent(AdminIntent.LoadWhitelist(searchText.ifBlank { null }))
            2 -> vm.onIntent(AdminIntent.LoadCourses)
        }
    }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let { snackbarHostState.showSnackbar(it); vm.onIntent(AdminIntent.ClearMessages) }
        state.successMessage?.let { snackbarHostState.showSnackbar(it); vm.onIntent(AdminIntent.ClearMessages) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { Text("🛡️ Yönetim Paneli", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                
                ScrollableTabRow(
                    selectedTabIndex = selectedTab, 
                    containerColor = Color.White, 
                    contentColor = Color(0xFF3B82F6),
                    edgePadding = 16.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                if (selectedTab == 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("Ara...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { csvPickerLauncher.launch("text/comma-separated-values") },
                            modifier = Modifier.background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = "CSV Yükle", tint = Color(0xFF3B82F6))
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != 0) {
                FloatingActionButton(
                    onClick = { if (selectedTab == 1) showAddUserDialog = true else showAddCourseDialog = true },
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                ) {
                    Icon(if (selectedTab == 1) Icons.Default.PersonAdd else Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC))) {
            if (state.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF3B82F6))

            when (selectedTab) {
                0 -> DashboardContent(state)
                1 -> WhitelistContent(
                    list = state.whitelist,
                    onDelete = { vm.onIntent(AdminIntent.RemoveFromWhitelist(it)) },
                    onToggleActive = { id, active -> vm.onIntent(AdminIntent.UpdateWhitelist(id, mapOf("is_active" to active))) }
                )
                2 -> CoursesAdminContent(
                    courses = state.courses,
                    onDelete = { vm.onIntent(AdminIntent.DeleteCourse(it)) },
                    onTogglePublish = { id, pub -> vm.onIntent(AdminIntent.UpdateCourse(id, mapOf("is_published" to pub))) },
                    onEdit = { courseToEdit = it }
                )
            }
        }

        // Dialogs...
        if (showAddUserDialog) AddUserDialog(onDismiss = { showAddUserDialog = false }, onConfirm = { e, s, c, n -> vm.onIntent(AdminIntent.AddToWhitelist(e, s, c, n)); showAddUserDialog = false })
        if (showAddCourseDialog) AddCourseDialog(onDismiss = { showAddCourseDialog = false }, onConfirm = { c -> vm.onIntent(AdminIntent.CreateCourse(c)); showAddCourseDialog = false })
        courseToEdit?.let { c -> EditCourseDialog(course = c, onDismiss = { courseToEdit = null }, onConfirm = { u -> vm.onIntent(AdminIntent.UpdateCourse(c.id, u)); courseToEdit = null }) }
    }
}

@Composable
fun DashboardContent(state: com.eduplatform.presentation.viewmodel.AdminState) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Sistem Özeti", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard("Toplam Üye", state.totalStudents.toString(), Icons.Default.People, Color(0xFF3B82F6), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard("Aktif Eğitim", state.publishedCoursesCount.toString(), Icons.Default.AutoStories, Color(0xFF10B981), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard("Kayıtlar", "0", Icons.Default.AppRegistration, Color(0xFF8B5CF6), Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            StatCard("Sertifika", "0", Icons.Default.Verified, Color(0xFFF59E0B), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Son Eklenen Eğitimler", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(12.dp))
        
        state.courses.take(3).forEach { course ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Book, null, tint = Color(0xFF3B82F6))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(course.title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF1E293B))
            Text(label, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun WhitelistContent(
    list: List<com.eduplatform.data.api.dto.WhitelistDto>, 
    onDelete: (String) -> Unit, 
    onToggleActive: (String, Boolean) -> Unit
) {
    if (list.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sonuç bulunamadı", color = Color.Gray)
        }
    }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(list) { item ->
            val id = item.id ?: ""
            val email = item.email ?: "E-posta Yok"
            val sicilNo = item.sicil_no ?: "-"
            val notes = item.notes ?: ""
            val isActive = item.is_active ?: true
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(email, fontWeight = FontWeight.Bold, color = if (isActive) Color.Black else Color.Gray)
                        Text("Sicil: $sicilNo", fontSize = 12.sp, color = Color.Gray)
                        if (notes.isNotBlank()) {
                            Text("Not: $notes", fontSize = 11.sp, color = Color(0xFF3B82F6), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { onToggleActive(id, it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF3B82F6))
                    )
                    IconButton(onClick = { onDelete(id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
fun CoursesAdminContent(courses: List<com.eduplatform.domain.model.Course>, onDelete: (String) -> Unit, onTogglePublish: (String, Boolean) -> Unit, onEdit: (com.eduplatform.domain.model.Course) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
        items(courses) { course ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(course.title, fontWeight = FontWeight.Bold)
                        Text("${course.category} • ${course.city}", fontSize = 12.sp, color = Color.Gray)
                    }
                    IconButton(onClick = { onEdit(course) }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF3B82F6))
                    }
                    Switch(
                        checked = course.isPublished,
                        onCheckedChange = { onTogglePublish(course.id, it) }
                    )
                    IconButton(onClick = { onDelete(course.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onConfirm: (String?, String?, String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var sicilNo by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Üye Ekle") },
        text = {
            Column {
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-posta") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sicilNo, onValueChange = { sicilNo = it }, label = { Text("Sicil No") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Şehir") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notlar") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(email, sicilNo, city, notes) }, enabled = email.isNotBlank() || sicilNo.isNotBlank()) { Text("Ekle") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
fun EditCourseDialog(course: com.eduplatform.domain.model.Course, onDismiss: () -> Unit, onConfirm: (Map<String, Any>) -> Unit) {
    var title by remember { mutableStateOf(course.title) }
    var desc by remember { mutableStateOf(course.description) }
    var category by remember { mutableStateOf(course.category) }
    var city by remember { mutableStateOf(course.city) }
    var instructor by remember { mutableStateOf(course.instructorName) }
    var thumb by remember { mutableStateOf(course.thumbnailUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eğitimi Düzenle") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Şehir") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructor, onValueChange = { instructor = it }, label = { Text("Eğitmen") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = thumb, onValueChange = { thumb = it }, label = { Text("Görsel URL") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(mapOf(
                    "title" to title,
                    "description" to desc,
                    "category" to category,
                    "city" to city,
                    "instructor_name" to instructor,
                    "thumbnail_url" to thumb
                ))
            }) { Text("Güncelle") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
fun AddCourseDialog(onDismiss: () -> Unit, onConfirm: (com.eduplatform.domain.model.Course) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var thumb by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Eğitim Oluştur") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Açıklama") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Şehir") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructor, onValueChange = { instructor = it }, label = { Text("Eğitmen") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = thumb, onValueChange = { thumb = it }, label = { Text("Görsel URL") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(
                    com.eduplatform.domain.model.Course(
                        id = "", 
                        title = title,
                        description = desc,
                        category = category,
                        city = city,
                        instructorName = instructor,
                        durationMinutes = 60,
                        hasCertificate = true,
                        isPublished = false,
                        thumbnailUrl = thumb.ifBlank { null }
                    )
                )
            }) { Text("Oluştur") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}
