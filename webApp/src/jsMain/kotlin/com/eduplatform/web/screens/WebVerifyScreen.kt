package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.*
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebVerifyScreen(code: String) {
    val koin = object : KoinComponent {}
    val certVM: CertViewModel by koin.inject()
    val state by certVM.state.collectAsState()

    LaunchedEffect(code) {
        certVM.onIntent(CertIntent.Verify(code))
    }

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            alignItems(AlignItems.Center)
            justifyContent(JustifyContent.Center)
            minHeight(70.vh)
        }
    }) {
        if (state.isLoading) {
            Div({ style { fontSize(20.px); color(Color("#64748B")) } }) { Text("Sertifika doğrulanıyor...") }
        } else if (state.selected != null) {
            val cert = state.selected!!
            Div({
                style {
                    maxWidth(600.px)
                    padding(48.px)
                    backgroundColor(Color.white)
                    borderRadius(16.px)
                    textAlign("center")
                    property("box-shadow", "0 20px 25px -5px rgba(0, 0, 0, 0.1)")
                    border(2.px, LineStyle.Solid, Color("#F59E0B"))
                }
            }) {
                Div({ style { fontSize(48.px); marginBottom(24.px) } }) { Text("🏆") }
                H1({ style { marginBottom(8.px); color(Color("#1E293B")) } }) { Text("Başarı Sertifikası") }
                P({ style { color(Color("#64748B")); marginBottom(32.px) } }) { Text("Bu sertifika başarıyla doğrulanmıştır.") }
                
                Div({
                    style {
                        fontSize(24.px)
                        fontWeight(700)
                        marginBottom(8.px)
                        color(Color("#1E293B"))
                    }
                }) { Text(cert.userName) }
                
                P({ style { marginBottom(32.px) } }) {
                    Text("${cert.courseTitle} eğitimini başarıyla tamamlayarak bu sertifikayı almaya hak kazanmıştır.")
                }
                
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        paddingTop(24.px)
                        property("border-top", "1px solid #E2E8F0")
                        fontSize(14.px)
                        color(Color("#94A3B8"))
                    }
                }) {
                    Div { Text("Tarih: ${cert.issuedAt}") }
                    Div { Text("Kod: ${cert.verifyCode}") }
                }
            }
        } else if (state.error != null) {
            Div({
                style {
                    textAlign("center")
                    color(Color("#DC2626"))
                }
            }) {
                H2 { Text("Geçersiz Sertifika") }
                P { Text("Belirttiğiniz doğrulama kodu ile eşleşen bir sertifika bulunamadı.") }
            }
        }
    }
}
