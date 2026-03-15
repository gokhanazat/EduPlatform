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
fun WebHomeScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val vm: CourseViewModel by koin.inject()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.onIntent(CourseIntent.Load)
    }

    Div({
        style {
            maxWidth(1200.px)
            property("margin", "0 auto")
            padding(40.px, 24.px)
        }
    }) {
        // Üst Başlık ve Bildirim Alanı
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                marginBottom(32.px)
            }
        }) {
            Div {
                H1({ style { fontSize(32.px); fontWeight(700); color(Color("#1E293B")); margin(0.px) } }) { Text("Eğitimlerim") }
                P({ style { color(Color("#64748B")); marginTop(8.px) } }) { Text("Öğrenme yolculuğunuza kaldığınız yerden devam edin.") }
            }
            // Bildirim İkonu (Android'deki gibi)
            Div({
                style {
                    position(Position.Relative)
                    padding(10.px)
                    borderRadius(12.px)
                    backgroundColor(Color.white)
                    cursor("pointer")
                    property("box-shadow", "0 1px 2px rgba(0,0,0,0.05)")
                }
            }) {
                Text("🔔")
                Div({
                    style {
                        position(Position.Absolute); top(8.px); right(8.px)
                        width(8.px); height(8.px); backgroundColor(Color.red); borderRadius(50.percent)
                    }
                }) {}
            }
        }

        // Arama Çubuğu
        Div({ style { marginBottom(40.px) } }) {
            Input(InputType.Search) {
                placeholder("Kurslarda veya eğitmenlerde ara...")
                value(state.searchQuery)
                onInput { vm.onIntent(CourseIntent.Search(it.value)) }
                style {
                    width(100.percent); padding(14.px, 20.px); borderRadius(12.px)
                    border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                    backgroundColor(Color.white); fontSize(16.px); outline("none")
                }
            }
        }

        // 2'li Grid Yapısı (Web'e uygun responsive ayarlı)
        Div({
            style {
                display(DisplayStyle.Grid)
                gap(24.px)
                // Masaüstünde 2 sütun (isteğin üzerine), mobilde 1 sütun
                property("grid-template-columns", "repeat(auto-fit, minmax(450.px, 1fr))")
            }
        }) {
            state.filteredCourses.forEach { course ->
                WebGridCourseCard(course, onNav)
            }
        }
    }
}

@Composable
fun WebGridCourseCard(course: com.eduplatform.domain.model.Course, onNav: (String) -> Unit) {
    Div({
        style {
            backgroundColor(Color.white); borderRadius(20.px); overflow("hidden")
            display(DisplayStyle.Flex); property("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            cursor("pointer"); property("transition", "transform 0.2s")
        }
        onClick { onNav("/course/${course.id}") }
    }) {
        // Sol Taraf: Görsel ve Badge
        Div({
            style {
                width(180.px); height(180.px); position(Position.Relative)
                backgroundImage("url('${course.thumbnailUrl}')")
                backgroundSize("cover"); backgroundPosition("center")
            }
        }) {
            // Tamamlanma Badge'i (Android'deki gibi)
            Span({
                style {
                    position(Position.Absolute); top(12.px); left(12.px)
                    backgroundColor(Color("#3B82F6")); color(Color.white)
                    fontSize(10.px); fontWeight(700); padding(4.px, 8.px); borderRadius(6.px)
                }
            }) { Text("%75 TAMAMLANDI") }
        }

        // Sağ Taraf: İçerik
        Div({
            style {
                padding(20.px); flex(1); display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column); justifyContent(JustifyContent.SpaceBetween)
            }
        }) {
            Div {
                H3({
                    style {
                        fontSize(18.px); fontWeight(700); color(Color("#1E293B"))
                        margin(0.px, 0.px, 8.px, 0.px)
                    }
                }) { Text(course.title) }
                
                P({
                    style {
                        fontSize(14.px); color(Color("#64748B")); lineHeight(1.4.em)
                        margin(0.px); property("display", "-webkit-box")
                        property("-webkit-line-clamp", "2"); property("-webkit-box-orient", "vertical")
                        overflow("hidden")
                    }
                }) { Text(course.description) }
            }

            Div({
                style {
                    display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center); marginTop(16.px)
                }
            }) {
                // Ders Sayısı
                Div({ style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(6.px); fontSize(13.px); color(Color("#475569")) } }) {
                    Span { Text("🔵") } // İkon placeholder
                    Text("12/16 Ders")
                }

                // Devam Et Butonu (Android'deki gibi)
                Button({
                    style {
                        padding(8.px, 20.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                        border(0.px); borderRadius(8.px); fontWeight(600); cursor("pointer")
                        fontSize(14.px)
                    }
                }) { Text("Devam Et") }
            }
        }
    }
}
