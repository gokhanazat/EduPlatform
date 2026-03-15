package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.User
import com.eduplatform.domain.repository.AuthRepository
import com.eduplatform.util.Result
import com.eduplatform.util.onSuccess
import com.eduplatform.util.onError

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
    val isLoggedIn: Boolean = false,
    val successEvent: Boolean = false
)

sealed class AuthIntent {
    data class SignIn(val email: String, val password: String) : AuthIntent()
    data class SignUp(val email: String, val password: String, val fullName: String) : AuthIntent()
    object SignOut : AuthIntent()
    object CheckAuth : AuthIntent()
    data class ResetPassword(val email: String) : AuthIntent()
    object ClearError : AuthIntent()
    object ClearEvent : AuthIntent()
    object AdminLogin : AuthIntent() // YENİ: Geçici Admin Girişi
}

class AuthViewModel(private val repository: AuthRepository) : BaseViewModel<AuthState, AuthIntent>(AuthState()) {

    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.AdminLogin -> {
                // GEÇİCİ: Seni direkt admin olarak içeri alır
                setState { copy(
                    isLoading = false,
                    isLoggedIn = true,
                    successEvent = true,
                    currentUser = User(
                        id = "admin-dev", 
                        email = "admin@eduplatform.com", 
                        fullName = "Geliştirici Admin", 
                        role = "admin",
                        sicilNo = "000000",
                        city = "Merkez"
                    )
                ) }
            }
            is AuthIntent.CheckAuth -> {
                val isLoggedIn = repository.isLoggedIn()
                setState { copy(isLoggedIn = isLoggedIn) }
                if (isLoggedIn) {
                    repository.getCurrentUser().onSuccess { user ->
                        setState { copy(currentUser = user) }
                    }
                }
            }
            is AuthIntent.SignIn -> {
                setState { copy(isLoading = true, error = null) }
                repository.signIn(intent.email, intent.password)
                    .onSuccess { user ->
                        setState { copy(isLoading = false, currentUser = user, isLoggedIn = true, successEvent = true) }
                    }
                    .onError { message ->
                        setState { copy(isLoading = false, error = message) }
                    }
            }
            is AuthIntent.SignUp -> {
                setState { copy(isLoading = true, error = null) }
                repository.signUp(intent.email, intent.password, intent.fullName)
                    .onSuccess { user ->
                        setState { copy(isLoading = false, currentUser = user, isLoggedIn = true, successEvent = true) }
                    }
                    .onError { message ->
                        setState { copy(isLoading = false, error = message) }
                    }
            }
            is AuthIntent.SignOut -> {
                repository.signOut()
                setState { copy(isLoggedIn = false, currentUser = null, successEvent = true) }
            }
            is AuthIntent.ResetPassword -> {
                setState { copy(isLoading = true, error = null) }
                repository.resetPassword(intent.email)
                    .onSuccess { setState { copy(isLoading = false, successEvent = true) } }
                    .onError { message -> setState { copy(isLoading = false, error = message) } }
            }
            is AuthIntent.ClearError -> setState { copy(error = null) }
            is AuthIntent.ClearEvent -> setState { copy(successEvent = false) }
        }
    }
}
