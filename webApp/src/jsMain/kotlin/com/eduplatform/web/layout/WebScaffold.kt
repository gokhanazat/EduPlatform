package com.eduplatform.web.layout

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import kotlinx.browser.window
import com.eduplatform.presentation.viewmodel.AuthViewModel
import com.eduplatform.presentation.viewmodel.AuthIntent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebScaffold(
    route: String,
    onNav: (String) -> Unit,
    isLoggedIn: Boolean,
    content: @Composable () -> Unit
) {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val authState by authVM.state.collectAsState()

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            height(100.vh)
            width(100.vw)
            backgroundColor(Color("#F8FAFC"))
        }
    }) {
        // Top Header
        Header({
            style {
                height(72.px); backgroundColor(Color.white); display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Center)
                padding(0.px, 60.px); property("box-shadow", "0 1px 2px rgba(0,0,0,0.05)"); property("z-index", "100")
            }
        }) {
            Div({ style { cursor("pointer"); fontWeight(700); fontSize(24.px) }; onClick { onNav("/") } }) { Text("EduPlatform") }
            Div({ style { display(DisplayStyle.Flex); gap(24.px); alignItems(AlignItems.Center) } }) {
                if (!isLoggedIn) {
                    Span({ style { cursor("pointer"); fontWeight(600) }; onClick { onNav("/login") } }) { Text("Giriş Yap") }
                    Button({
                        style { padding(10.px, 20.px); backgroundColor(Color("#3B82F6")); color(Color.white); border(0.px); borderRadius(8.px); cursor("pointer") }
                        onClick { onNav("/register") }
                    }) { Text("Kayıt Ol") }
                } else {
                    if (authState.currentUser?.role == "admin") {
                        Span({ 
                            style { cursor("pointer"); fontWeight(700); color(Color("#E11D48")) } 
                            onClick { onNav("/admin") } 
                        }) { Text("🛡️ Admin Paneli") }
                    }
                    Span({ style { cursor("pointer"); fontWeight(600) }; onClick { onNav("/home") } }) { Text("Eğitimlerim") }
                    Span({ style { cursor("pointer"); fontWeight(600) }; onClick { onNav("/profile") } }) { Text("Profil") }
                    Button({
                        style { padding(8.px, 16.px); backgroundColor(Color("#F1F5F9")); border(0.px); borderRadius(8.px); cursor("pointer") }
                        onClick { 
                            authVM.onIntent(AuthIntent.SignOut)
                            onNav("/") 
                        } 
                    }) { Text("Çıkış") }
                }
            }
        }

        Div({
            style { display(DisplayStyle.Flex); flex(1); overflow("hidden") }
        }) {

        // Main Content Area
        Main({
            style {
                flex(1)
                backgroundColor(Color("#F8FAFC"))
                overflowY("auto")
                padding(0.px)
            }
        }) {
            content()
        }
        }
    }
}
