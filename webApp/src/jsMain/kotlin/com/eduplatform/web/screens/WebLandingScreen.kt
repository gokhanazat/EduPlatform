package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.presentation.viewmodel.CourseIntent
import com.eduplatform.presentation.viewmodel.CourseViewModel
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebLandingScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val vm: CourseViewModel by koin.inject()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.onIntent(CourseIntent.Load)
    }

    Div({ style { backgroundColor(Color("#F8FAFC")); minHeight(100.vh) } }) {
        
        // 1. HERO SECTION
        Section({
            style {
                padding(80.px, 60.px)
                background("linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url('https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=1471&q=80')")
                property("background-size", "cover")
                property("background-position", "center")
                borderRadius(24.px)
                margin(20.px, 60.px)
                color(Color.white)
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                justifyContent(JustifyContent.Center)
            }
        }) {
            H1({ style { fontSize(48.px); fontWeight(700); maxWidth(600.px); marginBottom(24.px); property("line-height", "1.2") } }) {
                Text("Dünya Standartlarında Uzmanlarla Yeni Beceriler Kazanın")
            }
            P({ style { fontSize(18.px); property("opacity", "0.9"); marginBottom(40.px); maxWidth(500.px) } }) {
                Text("Programlama, iş dünyası, tasarım ve daha pek çok kategoride 5.000'den fazla eğitime hemen erişin.")
            }
            Div({ style { display(DisplayStyle.Flex); gap(16.px) } }) {
                Button({
                    style {
                        padding(14.px, 32.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                        border(0.px); borderRadius(8.px); fontWeight(600); cursor("pointer")
                    }
                    onClick { onNav("/register") }
                }) { Text("Hemen Başla") }
                Button({
                    style {
                        padding(14.px, 32.px); backgroundColor(Color.white); color(Color("#1E293B"))
                        border(0.px); borderRadius(8.px); fontWeight(600); cursor("pointer")
                    }
                    onClick { onNav("/home") }
                }) { Text("Kütüphaneye Göz At") }
            }
        }

        // 2. KATEGORİLER VE ARAMA (Pills + Search)
        Section({ 
            style { 
                padding(0.px, 60.px, 40.px, 60.px)
            } 
        }) {
            Div({
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    gap(24.px)
                    backgroundColor(Color.white)
                    padding(16.px, 24.px)
                    borderRadius(24.px)
                    property("box-shadow", "0 4px 20px rgba(0,0,0,0.03)")
                }
            }) {
                // Sol Taraf: Kategori Pill'leri
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        gap(10.px)
                        property("overflow-x", "auto")
                        property("flex", "1")
                        property("scrollbar-width", "none") // Firefox
                        property("-ms-overflow-style", "none") // IE/Edge
                    }
                }) {
                    // Hepsi Filtresi
                    Div({
                        style {
                            padding(10.px, 24.px)
                            borderRadius(12.px)
                            fontSize(14.px)
                            fontWeight(700)
                            cursor("pointer")
                            backgroundColor(if (state.selectedCategory == null) Color("#3B82F6") else Color("#F1F5F9"))
                            color(if (state.selectedCategory == null) Color.white else Color("#64748B"))
                            property("transition", "all 0.2s")
                            property("white-space", "nowrap")
                        }
                        onClick { vm.onIntent(CourseIntent.FilterCategory("Hepsi")) }
                    }) { Text("Hepsi") }

                    state.categories.forEach { category ->
                        val isSelected = state.selectedCategory == category
                        Div({
                            style {
                                padding(10.px, 24.px)
                                borderRadius(12.px)
                                fontSize(14.px)
                                fontWeight(700)
                                cursor("pointer")
                                backgroundColor(if (isSelected) Color("#3B82F6") else Color.white)
                                color(if (isSelected) Color.white else Color("#64748B"))
                                property("border", "1px solid " + if (isSelected) "#3B82F6" else "#F1F5F9")
                                property("transition", "all 0.2s")
                                property("white-space", "nowrap")
                            }
                            onClick { vm.onIntent(CourseIntent.FilterCategory(category)) }
                        }) { Text(category) }
                    }
                }

                // Sağ Taraf: Arama Girişi
                Div({
                    style {
                        position(Position.Relative)
                        width(320.px)
                    }
                }) {
                    Input(InputType.Search) {
                        placeholder("Eğitim başlığı ile ara...")
                        value(state.searchQuery)
                        onInput { vm.onIntent(CourseIntent.Search(it.value)) }
                        style {
                            width(100.percent)
                            padding(12.px, 20.px)
                            borderRadius(16.px)
                            border(1.px, LineStyle.Solid, Color("#F1F5F9"))
                            backgroundColor(Color("#F8FAFC"))
                            fontSize(14.px)
                            outline("none")
                        }
                    }
                }
            }
        }

        // 3. POPÜLER EĞİTİMLER
        Section({ style { padding(0.px, 60.px, 60.px, 60.px) } }) {
            H2({ style { fontWeight(700); marginBottom(32.px) } }) { 
                Text(if (state.searchQuery.isNotEmpty()) "Arama Sonuçları" else "Öne Çıkan Eğitimler") 
            }
            
            Div({
                style {
                    display(DisplayStyle.Grid)
                    gap(24.px)
                    property("grid-template-columns", "repeat(auto-fill, minmax(280.px, 1fr))")
                }
            }) {
                state.filteredCourses.forEach { course ->
                    LandingCourseCard(course, onNav)
                }
            }

            if (state.filteredCourses.isEmpty()) {
                Div({ style { textAlign("center"); padding(40.px); color(Color.gray) } }) {
                    Text("Sonuç bulunamadı.")
                }
            }
        }

        // 4. STAY IN THE LOOP (Bülten)
        Section({
            style {
                textAlign("center"); padding(80.px, 20.px)
                backgroundColor(Color.white); property("border-top", "1px solid #E2E8F0")
            }
        }) {
            H2({ style { marginBottom(12.px); fontWeight(700) } }) { Text("Haberdar Olun") }
            P({ style { color(Color("#64748B")); marginBottom(32.px) } }) { Text("Yeni kurslar ve indirimlerden haberdar olmak için bültenimize kaydolun.") }
            Div({ style { display(DisplayStyle.Flex); justifyContent(JustifyContent.Center); gap(12.px) } }) {
                Input(InputType.Email) {
                    placeholder("E-posta adresiniz")
                    style {
                        padding(12.px, 20.px); borderRadius(8.px); border(1.px, LineStyle.Solid, Color("#CBD5E1"))
                        width(300.px)
                    }
                }
                Button({
                    style {
                        padding(12.px, 24.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                        border(0.px); borderRadius(8.px); fontWeight(600); cursor("pointer")
                    }
                }) { Text("Abone Ol") }
            }
        }

        // 5. FOOTER
        Footer({
            style {
                padding(60.px); backgroundColor(Color("#1E293B")); color(Color.white)
            }
        }) {
            Div({
                style {
                    display(DisplayStyle.Grid); gap(40.px)
                    property("grid-template-columns", "repeat(auto-fit, minmax(200.px, 1fr))")
                }
            }) {
                Div {
                    H3({ style { marginBottom(20.px) } }) { Text("EduPlatform") }
                    P({ style { fontSize(14.px); property("opacity", "0.7"); property("line-height", "1.6") } }) {
                        Text("Dünyanın her yerindeki milyonlarca insana yeni beceriler kazandırmak ve hayatlarını eğitimle değiştirmek için çalışıyoruz.")
                    }
                }
                FooterLinks("Platform", listOf("Kurslar", "Kurumsal", "Mobil Uygulama"))
                FooterLinks("Şirket", listOf("Hakkımızda", "Kariyer", "İletişim"))
                FooterLinks("Destek", listOf("Yardım Merkezi", "Kullanım Şartları", "Gizlilik"))
            }
            Div({
                style {
                    marginTop(60.px); paddingTop(30.px); property("border-top", "1px solid rgba(255,255,255,0.1)")
                    fontSize(12.px); property("opacity", "0.5"); textAlign("center")
                }
            }) {
                Text("© 2024 EduPlatform Inc. Tüm hakları saklıdır.")
            }
        }
    }
}

@Composable
fun LandingCourseCard(course: com.eduplatform.domain.model.Course, onNav: (String) -> Unit) {
    Div({
        style {
            backgroundColor(Color.white); borderRadius(16.px); overflow("hidden")
            property("box-shadow", "0 4px 6px rgba(0,0,0,0.05)"); cursor("pointer")
        }
        onClick { onNav("/course/${course.id}") }
    }) {
        Img(src = course.thumbnailUrl ?: "", attrs = { style { width(100.percent); height(160.px); property("object-fit", "cover") } })
        Div({ style { padding(20.px) } }) {
            Div({ style { color(Color("#3B82F6")); fontSize(12.px); fontWeight(600); marginBottom(8.px) } }) { Text(course.category) }
            Div({ style { fontWeight(700); fontSize(16.px); marginBottom(12.px); height(40.px); overflow("hidden") } }) { Text(course.title) }
            Div({ style { display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween); alignItems(AlignItems.Center) } }) {
                Span({ style { fontWeight(700); color(Color("#1E293B")) } }) { Text("Ücretsiz") }
                Span({ style { fontSize(12.px); color(Color("#64748B")) } }) { Text("⭐ 4.8") }
            }
        }
    }
}

@Composable
fun FooterLinks(title: String, links: List<String>) {
    Div {
        H4({ style { marginBottom(20.px); fontSize(16.px) } }) { Text(title) }
        links.forEach { link ->
            Div({ style { fontSize(14.px); property("opacity", "0.7"); marginBottom(10.px); cursor("pointer") } }) { Text(link) }
        }
    }
}
