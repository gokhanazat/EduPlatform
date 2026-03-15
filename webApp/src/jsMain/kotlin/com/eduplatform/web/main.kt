package com.eduplatform.web

import com.eduplatform.di.sharedModule
import com.eduplatform.web.di.webModule
import com.eduplatform.web.routing.EduWebApp
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.*
import org.koin.core.context.startKoin

fun main() {
    println("EduPlatform Web starting...")
    try {
        startKoin {
            modules(sharedModule, webModule)
        }
        println("Koin initialization successful")
        
        renderComposable(rootElementId = "root") {
            println("Rendering EduWebApp...")
            EduWebApp()
            
            // Debug Label (will be removed later)
            Div({
                style {
                    position(Position.Fixed)
                    bottom(10.px)
                    right(10.px)
                    padding(4.px, 8.px)
                    backgroundColor(Color("#1E293B"))
                    color(Color.white)
                    borderRadius(4.px)
                    fontSize(10.px)
                    property("z-index", "1000")
                    property("pointer-events", "none")
                }
            }) {
                Text("v1.0.1 - Connected to Supabase")
            }
        }
    } catch (e: Exception) {
        println("CRITICAL ERROR during startup: ${e.message}")
        e.printStackTrace()
        renderComposable(rootElementId = "root") {
            Div({
                style {
                    padding(20.px)
                    color(Color.red)
                    fontFamily("sans-serif")
                }
            }) {
                H1 { Text("Uygulama Başlatılamadı") }
                P { Text("Hata: ${e.message}") }
                P { Text("Lütfen tarayıcı konsolunu (F12) kontrol edin.") }
            }
        }
    }
}
