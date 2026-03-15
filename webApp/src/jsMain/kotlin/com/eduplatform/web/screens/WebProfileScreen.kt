package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebProfileScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val authVM: AuthViewModel by koin.inject()
    val courseVM: CourseViewModel by koin.inject()
    val certVM: CertViewModel by koin.inject()

    val authState by authVM.state.collectAsState()
    val courseState by courseVM.state.collectAsState()
    val certState by certVM.state.collectAsState()

    val user = authState.currentUser
    val enrolledCourses = courseState.courses.filter { it.id in courseState.enrolledCourseIds }

    LaunchedEffect(user?.id) {
        user?.id?.let { certVM.onIntent(CertIntent.Load(it)) }
    }

    Div({
        style {
            maxWidth(1100.px)
            property("margin", "0 auto")
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            gap(32.px)
        }
    }) {
        // 1. ÜST PROFİL KARTI
        Div({
            style {
                backgroundColor(Color.white)
                padding(32.px)
                borderRadius(16.px)
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                property("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
            }
        }) {
            Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(24.px) } }) {
                // Profil Resmi (Avatar)
                Div({
                    style {
                        width(100.px); height(100.px); borderRadius(50.percent)
                        backgroundColor(Color("#F3F4F6"))
                        position(Position.Relative)
                        backgroundImage("url('https://ui-avatars.com/api/?name=${user?.fullName ?: "User"}&size=200')")
                        backgroundSize("cover")
                        backgroundPosition("center")
                    }
                }) {
                    // Online Durum İkonu (Yeşil Nokta)
                    Div({
                        style {
                            position(Position.Absolute); bottom(5.px); right(5.px)
                            width(15.px); height(15.px); backgroundColor(Color("#10B981"))
                            borderRadius(50.percent); border(2.px, LineStyle.Solid, Color.white)
                        }
                    }) {}
                }

                Div {
                    H2({ style { margin(0.px); fontSize(24.px); fontWeight(700) } }) { Text(user?.fullName ?: "Kullanıcı Adı") }
                    Div({ style { color(Color("#3B82F6")); fontWeight(500); marginBottom(8.px) } }) { Text("Lifelong Learner") }
                    Div({ style { display(DisplayStyle.Flex); gap(16.px); color(Color("#64748B")); fontSize(14.px) } }) {
                        Span { Text("🆔 Sicil No: ${user?.sicilNo ?: "Belirtilmemiş"}") }
                        Span { Text("📅 Mart 2023'te Katıldı") }
                    }
                }
            }

            Button({
                style {
                    padding(10.px, 20.px); border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                    borderRadius(8.px); backgroundColor(Color.white); fontWeight(600); cursor("pointer")
                }
            }) { Text("Profili Düzenle") }
        }

        // 2. İSTATİSTİK KARTLARI (Yanyana)
        Div({ style { display(DisplayStyle.Flex); gap(24.px) } }) {
            WebStatCard("Kayıtlı Eğitimler", courseState.enrolledCourseIds.size.toString(), "#3B82F6", "https://img.icons8.com/ios-filled/50/3B82F6/education.png")
            WebStatCard("Sertifikalar", certState.certificates.size.toString(), "#F59E0B", "https://img.icons8.com/ios-filled/50/F59E0B/guarantee.png")
        }

        // 3. EĞİTİMLERİM LİSTESİ
        Div {
            Div({
                style {
                    display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center); marginBottom(20.px)
                }
            }) {
                H2({ style { margin(0.px); fontSize(20.px); fontWeight(700) } }) { Text("Eğitimlerim") }
                Span({ 
                    style { color(Color("#3B82F6")); cursor("pointer"); fontWeight(600); fontSize(14.px) }
                    onClick { onNav("/home") }
                }) { Text("Tümünü Gör") }
            }

            Div({ style { display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); gap(16.px) } }) {
                if (enrolledCourses.isEmpty()) {
                    P({ style { color(Color("#64748B")) } }) { Text("Henüz bir eğitime kayıt olmadınız.") }
                } else {
                    enrolledCourses.forEach { course ->
                        WebCourseProgressItem(course, 0.8f) // Örnek progress
                    }
                }
            }
        }
    }
}

@Composable
fun WebStatCard(label: String, value: String, accentColor: String, iconUrl: String) {
    Div({
        style {
            flex(1); backgroundColor(Color.white); padding(24.px); borderRadius(16.px)
            display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Center)
            property("box-shadow", "0 1px 2px rgba(0,0,0,0.05)")
        }
    }) {
        Div {
            Div({ style { fontSize(14.px); color(Color("#64748B")); marginBottom(8.px); fontWeight(500) } }) { Text(label) }
            Div({ style { fontSize(32.px); fontWeight(700) } }) { Text(value) }
        }
        Img(src = iconUrl, attrs = { style { width(40.px); property("opacity", "0.8") } })
    }
}

@Composable
fun WebCourseProgressItem(course: com.eduplatform.domain.model.Course, progress: Float) {
    Div({
        style {
            backgroundColor(Color.white); padding(20.px); borderRadius(12.px)
            display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(20.px)
            border(1.px, LineStyle.Solid, Color("#F1F5F9"))
        }
    }) {
        // Kurs İkonu/Görseli
        Div({
            style {
                width(48.px); height(48.px); borderRadius(8.px)
                backgroundColor(Color("#EEF2FF")); display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center); alignItems(AlignItems.Center)
            }
        }) {
            Img(src = "https://img.icons8.com/fluency/48/code.png", attrs = { style { width(24.px) } })
        }

        // Bilgiler ve Progress
        Div({ style { flex(1) } }) {
            Div({
                style {
                    display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween)
                    marginBottom(8.px)
                }
            }) {
                Div({ style { fontWeight(600); fontSize(16.px) } }) { Text(course.title) }
                Div({
                    style {
                        fontSize(11.px); fontWeight(700); padding(2.px, 8.px)
                        borderRadius(4.px); backgroundColor(if (progress >= 1f) Color("#DCFCE7") else Color("#DBEAFE"))
                        color(if (progress >= 1f) Color("#166534") else Color("#1E40AF"))
                    }
                }) { Text(if (progress >= 1f) "TAMAMLANDI" else "DEVAM EDİYOR") }
            }

            // Progress Bar
            Div({
                style {
                    width(100.percent); height(8.px); backgroundColor(Color("#F1F5F9"))
                    borderRadius(4.px); overflow("hidden"); position(Position.Relative)
                }
            }) {
                Div({
                    style {
                        width((progress * 100).percent); height(100.percent)
                        backgroundColor(if (progress >= 1f) Color("#10B981") else Color("#3B82F6"))
                        property("transition", "width 0.5s")
                    }
                }) {}
            }

            Div({
                style {
                    display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween)
                    marginTop(8.px); fontSize(12.px); color(Color("#94A3B8"))
                }
            }) {
                Text("%${(progress * 100).toInt()} Tamamlandı")
                Text("12/15 Ders")
            }
        }

        // Aksiyon Butonu
        Div({
            style {
                width(40.px); height(40.px); borderRadius(50.percent)
                backgroundColor(if (progress >= 1f) Color("#10B981") else Color("#3B82F6"))
                display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Center)
                cursor("pointer"); color(Color.white); fontWeight(700)
            }
        }) {
            Span({ style { property("margin", "auto") } }) { 
                Text(if (progress >= 1f) "👁" else "▶") 
            }
        }
    }
}
