package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebCertScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val certVM: CertViewModel by koin.inject()

    val authState by authVM.state.collectAsState()
    val certState by certVM.state.collectAsState()
    val userId = authState.currentUser?.id ?: ""

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            certVM.onIntent(CertIntent.Load(userId))
        }
    }

    Div({
        style {
            maxWidth(800.px)
            property("margin", "0 auto")
        }
    }) {
        H1({ style { marginBottom(32.px) } }) { Text("Sertifikalarım") }

        if (certState.isLoading) {
            Div({ style { textAlign("center"); padding(40.px) } }) { Text("Yükleniyor...") }
        } else if (certState.certificates.isEmpty()) {
            Div({
                style {
                    backgroundColor(Color.white)
                    padding(40.px)
                    borderRadius(12.px)
                    textAlign("center")
                    color(Color("#64748B"))
                    property("box-shadow", "0 1px 3px 0 rgba(0, 0, 0, 0.1)")
                }
            }) {
                Text("Henüz bir sertifikanız bulunmuyor. Eğitimleri tamamlayarak sertifika kazanabilirsiniz.")
            }
        } else {
            Div({ style { display(DisplayStyle.Grid); gap(24.px) } }) {
                certState.certificates.forEach { cert ->
                    Div({
                        style {
                            backgroundColor(Color.white)
                            padding(24.px)
                            borderRadius(16.px)
                            display(DisplayStyle.Flex)
                            alignItems(AlignItems.Center)
                            gap(20.px)
                            property("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
                        }
                    }) {
                        // Icon placeholder
                        Div({
                            style {
                                width(60.px); height(60.px); borderRadius(12.px)
                                backgroundColor(Color("#FEF3C7"))
                                display(DisplayStyle.Flex); alignItems(AlignItems.Center); justifyContent(JustifyContent.Center)
                                fontSize(30.px)
                            }
                        }) { Text("📜") }

                        Div({ style { flex(1) } }) {
                            Div({ style { fontWeight(700); fontSize(18.px); marginBottom(4.px) } }) { Text(cert.courseTitle) }
                            Div({ style { fontSize(14.px); color(Color("#64748B")) } }) { 
                                Text("Veriliş Tarihi: ${cert.issuedAt}") 
                             }
                        }

                        Button({
                            style {
                                padding(8.px, 16.px)
                                backgroundColor(Color("#4F46E5"))
                                color(Color.white)
                                border(0.px)
                                borderRadius(6.px)
                                fontWeight(600)
                                cursor("pointer")
                            }
                            onClick { onNav("/verify/${cert.verifyCode}") }
                        }) { Text("Görüntüle") }
                    }
                }
            }
        }
    }
}
