package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.AuthIntent
import com.eduplatform.presentation.viewmodel.AuthViewModel
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebLoginScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val vm: AuthViewModel by koin.inject()
    val state by vm.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    // Üst Navigasyon Çubuğu (Görseldeki gibi)
    Header({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.SpaceBetween)
            alignItems(AlignItems.Center)
            padding(20.px, 60.px)
            backgroundColor(Color.white)
            position(Position.Absolute)
            top(0.px)
            width(100.percent)
            property("z-index", "10")
        }
    }) {
        Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(10.px) } }) {
            // Logo
            Div({
                style {
                    width(32.px); height(32.px); backgroundColor(Color("#3B82F6")); borderRadius(6.px)
                }
            }) {}
            Span({ style { fontWeight(700); fontSize(20.px); color(Color("#1E293B")) } }) { Text("EduPlatform") }
        }

        Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(30.px) } }) {
            Span({ 
                style { cursor("pointer"); fontSize(14.px); fontWeight(500) }
                onClick { onNav("/") }
            }) { Text("Ana Sayfa") }
            Span({ style { cursor("pointer"); fontSize(14.px); fontWeight(500) } }) { Text("Eğitimler") }
            Span({ style { cursor("pointer"); fontSize(14.px); fontWeight(500) } }) { Text("Hakkımızda") }
            Button({
                style {
                    padding(8.px, 20.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                    border(0.px); borderRadius(6.px); fontWeight(600); cursor("pointer")
                }
                onClick { onNav("/register") }
            }) { Text("Kayıt Ol") }
        }
    }

    // Ana İçerik Konteynırı
    Div({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            minHeight(100.vh)
            backgroundColor(Color("#F8FAFC"))
            paddingTop(80.px)
        }
    }) {
        // Ana Kart (Görseldeki Split Tasarım)
        Div({
            style {
                display(DisplayStyle.Flex)
                width(1000.px)
                height(600.px)
                backgroundColor(Color.white)
                borderRadius(16.px)
                property("box-shadow", "0 25px 50px -12px rgba(0, 0, 0, 0.1)")
                overflow("hidden")
            }
        }) {
            // SOL TARAF: Görsel ve Metin
            Div({
                style {
                    flex(1)
                    position(Position.Relative)
                    backgroundImage("url('https://images.unsplash.com/photo-1522202176988-66273c2fd55f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1471&q=80')")
                    backgroundSize("cover")
                    backgroundPosition("center")
                }
            }) {
                // Mavi Overlay
                Div({
                    style {
                        position(Position.Absolute); top(0.px); left(0.px); width(100.percent); height(100.percent)
                        background("linear-gradient(rgba(59, 130, 246, 0.5), rgba(29, 78, 216, 0.8))")
                        display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); justifyContent(JustifyContent.FlexEnd)
                        padding(40.px)
                    }
                }) {
                    H2({ style { color(Color.white); marginBottom(16.px); fontSize(32.px); fontWeight(700) } }) { 
                        Text("Kariyerinizi Yükseltin") 
                    }
                    P({ style { color(rgba(255, 255, 255, 0.9)); fontSize(16.px); property("line-height", "1.5"); maxWidth(350.px) } }) {
                        Text("Teknoloji, tasarım ve iş dünyasında en çok talep edilen becerileri öğrenen 10.000'den fazla öğrenciye katılın.")
                    }
                }
            }

            // SAĞ TARAF: Giriş Formu
            Div({
                style {
                    flex(1)
                    padding(60.px)
                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Column)
                    justifyContent(JustifyContent.Center)
                }
            }) {
                H2({ style { marginBottom(8.px); fontSize(28.px); fontWeight(700) } }) { Text("Tekrar Hoş Geldiniz") }
                P({ style { color(Color("#64748B")); marginBottom(32.px); fontSize(14.px) } }) { 
                    Text("Giriş yapmak için lütfen bilgilerinizi girin.") 
                }

                if (state.error != null) {
                    Div({ style { padding(12.px); backgroundColor(Color("#FEE2E2")); color(Color("#DC2626")); borderRadius(8.px); marginBottom(20.px); fontSize(13.px) } }) {
                        Text(state.error!!)
                    }
                }

                // Sicil No
                Div({ style { marginBottom(20.px) } }) {
                    Label(forId = "email", attrs = { style { display(DisplayStyle.Block); marginBottom(8.px); fontSize(14.px); fontWeight(600) } }) { Text("Sicil Numarası") }
                    Input(InputType.Text) {
                        id("email")
                        value(email)
                        placeholder("Sicil numaranızı girin")
                        onInput { email = it.value }
                        style {
                            width(100.percent); padding(12.px); borderRadius(8.px); border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                            fontSize(14.px)
                        }
                    }
                }

                // Password
                Div({ style { marginBottom(12.px) } }) {
                    Div({ style { display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); marginBottom(8.px) } }) {
                        Label(forId = "pass", attrs = { style { fontSize(14.px); fontWeight(600) } }) { Text("Şifre") }
                        Span({ 
                            style { fontSize(12.px); color(Color("#3B82F6")); cursor("pointer"); fontWeight(600) }
                            onClick { onNav("/forgot") }
                        }) { Text("Şifremi Unuttum?") }
                    }
                    Input(InputType.Password) {
                        id("pass")
                        value(password)
                        placeholder("••••••••")
                        onInput { password = it.value }
                        style {
                            width(100.percent); padding(12.px); borderRadius(8.px); border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                            fontSize(14.px)
                        }
                    }
                }

                // Remember Me
                Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(8.px); marginBottom(32.px) } }) {
                    Input(InputType.Checkbox) {
                        checked(rememberMe)
                        onInput { rememberMe = it.value }
                    }
                    Span({ style { fontSize(13.px); color(Color("#64748B")) } }) { Text("30 gün boyunca beni hatırla") }
                }

                // Sign In Button
                Button({
                    style {
                        width(100.percent); padding(14.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                        border(0.px); borderRadius(8.px); fontWeight(600); cursor("pointer"); fontSize(16.px)
                    }
                    onClick { vm.onIntent(AuthIntent.SignIn(email, password)) }
                }) {
                    if (state.isLoading) Text("Giriş Yapılıyor...") else Text("Giriş Yap")
                }

                // Footer Link
                Div({ style { marginTop(32.px); textAlign("center"); fontSize(14.px); color(Color("#64748B")) } }) {
                    Text("Hesabınız yok mu? ")
                    Span({
                        style { color(Color("#3B82F6")); cursor("pointer"); fontWeight(600) }
                        onClick { onNav("/register") }
                    }) { Text("Hesap Oluştur") }
                }
            }
        }
    }

    // Footer Alt Metni
    Footer({
        style {
            position(Position.Absolute); bottom(20.px); width(100.percent); textAlign("center")
            color(Color("#94A3B8")); fontSize(12.px)
        }
    }) {
        Text("© 2024 EduPlatform Inc. Tüm hakları saklıdır. Profesyonel öğrenenler için profesyonel araçlar.")
    }
}
