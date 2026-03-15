package com.eduplatform.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen
import com.eduplatform.android.ui.components.EduButton
import com.eduplatform.android.ui.components.EduTextField
import com.eduplatform.presentation.viewmodel.AuthIntent
import com.eduplatform.presentation.viewmodel.AuthViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    val vm: AuthViewModel = koinInject()
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.successEvent) {
        if (state.successEvent) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = true }
            }
            vm.onIntent(AuthIntent.ClearEvent)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            val message = if (it.contains("422") || it.contains("bulunamadı")) {
                "Bu sicil numarası kayıt için yetkili değil veya zaten kayıtlı."
            } else it
            snackbarHostState.showSnackbar(message)
            vm.onIntent(AuthIntent.ClearError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Kayıt Ol") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EduTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Ad Soyad",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            EduTextField(
                value = email,
                onValueChange = { email = it },
                label = "Sicil Numarası",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            EduTextField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(12.dp))

            EduTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Şifre Tekrar",
                isPassword = true,
                error = localError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(32.dp))

            EduButton(
                text = "Kayıt Ol",
                onClick = {
                    localError = when {
                        password.length < 6 -> "Şifre en az 6 karakter olmalı"
                        password != confirmPassword -> "Şifreler eşleşmiyor"
                        else -> null
                    }
                    if (localError == null && fullName.isNotBlank() && email.isNotBlank()) {
                        vm.onIntent(AuthIntent.SignUp(email, password, fullName))
                    }
                },
                isLoading = state.isLoading,
                enabled = fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zaten hesabın var mı?")
                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text("Giriş Yap")
                }
            }
        }
    }
}
