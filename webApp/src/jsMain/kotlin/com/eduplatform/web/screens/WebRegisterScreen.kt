package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.AuthIntent
import com.eduplatform.presentation.viewmodel.AuthViewModel
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebRegisterScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val vm: AuthViewModel by koin.inject()
    val state by vm.state.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            minHeight(80.vh)
        }
    }) {
        Div({
            style {
                width(450.px)
                padding(40.px)
                backgroundColor(Color.white)
                borderRadius(12.px)
                property("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            }
        }) {
            H2({ style { marginBottom(24.px); textAlign("center") } }) { Text("Kayıt Ol") }

            val displayError = localError ?: state.error
            if (displayError != null) {
                Div({
                    style {
                        padding(12.px)
                        backgroundColor(Color("#FEE2E2"))
                        color(Color("#DC2626"))
                        borderRadius(6.px)
                        marginBottom(16.px)
                        fontSize(14.px)
                    }
                }) { 
                    val msg = if (displayError.contains("422") || displayError.contains("bulunamadı")) 
                        "Bu sicil numarası kayıt için yetkili değil veya zaten kayıtlı." else displayError
                    Text(msg) 
                }
            }

            // Full Name
            Div({ style { marginBottom(16.px) } }) {
                Label(forId = "fullName", attrs = { style { display(DisplayStyle.Block); marginBottom(4.px); fontSize(14.px) } }) { Text("Ad Soyad") }
                Input(InputType.Text) {
                    id("fullName")
                    value(fullName)
                    onInput { fullName = it.value }
                    style {
                        width(100.percent)
                        padding(10.px)
                        borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    }
                }
            }

            // Sicil No
            Div({ style { marginBottom(16.px) } }) {
                Label(forId = "email", attrs = { style { display(DisplayStyle.Block); marginBottom(4.px); fontSize(14.px) } }) { Text("Sicil Numarası") }
                Input(InputType.Text) {
                    id("email")
                    value(email)
                    onInput { email = it.value }
                    style {
                        width(100.percent)
                        padding(10.px)
                        borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    }
                }
            }

            // Password
            Div({ style { marginBottom(16.px) } }) {
                Label(forId = "password", attrs = { style { display(DisplayStyle.Block); marginBottom(4.px); fontSize(14.px) } }) { Text("Şifre") }
                Input(InputType.Password) {
                    id("password")
                    value(password)
                    onInput { password = it.value }
                    style {
                        width(100.percent)
                        padding(10.px)
                        borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    }
                }
            }

            // Confirm Password
            Div({ style { marginBottom(24.px) } }) {
                Label(forId = "confirm", attrs = { style { display(DisplayStyle.Block); marginBottom(4.px); fontSize(14.px) } }) { Text("Şifre Tekrar") }
                Input(InputType.Password) {
                    id("confirm")
                    value(confirmPassword)
                    onInput { confirmPassword = it.value }
                    style {
                        width(100.percent)
                        padding(10.px)
                        borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                    }
                }
            }

            Button({
                style {
                    width(100.percent)
                    padding(12.px)
                    backgroundColor(Color("#4F46E5"))
                    color(Color.white)
                    border(0.px)
                    borderRadius(6.px)
                    fontWeight(600)
                    cursor("pointer")
                }
                onClick {
                    localError = when {
                        password.length < 6 -> "Şifre en az 6 karakter olmalı"
                        password != confirmPassword -> "Şifreler eşleşmiyor"
                        else -> null
                    }
                    if (localError == null) {
                        vm.onIntent(AuthIntent.SignUp(email, password, fullName))
                    }
                }
            }) {
                if (state.isLoading) Text("Kayıt Yapılıyor...") else Text("Kayıt Ol")
            }

            Div({
                style {
                    marginTop(20.px)
                    textAlign("center")
                    fontSize(14.px)
                    color(Color("#64748B"))
                }
            }) {
                Text("Zaten hesabın var mı? ")
                Span({
                    style { color(Color("#4F46E5")); cursor("pointer"); fontWeight(600) }
                    onClick { onNav("/login") }
                }) { Text("Giriş Yap") }
            }
        }
    }
}
