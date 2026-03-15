package com.eduplatform.web.screens

import androidx.compose.runtime.*
import com.eduplatform.data.api.ApiConfig
import com.eduplatform.data.api.handleApiCall
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun WebAdminScreen(onNav: (String) -> Unit) {
    val koin = object : KoinComponent {}
    val client: HttpClient by koin.inject()
    val scope = rememberCoroutineScope()
    
    var whitelist by remember { mutableStateOf<List<com.eduplatform.data.api.dto.WhitelistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    var newEmail by remember { mutableStateOf("") }
    var newSicilNo by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
 
    fun loadWhitelist() {
        scope.launch {
            isLoading = true
            val result: Result<List<com.eduplatform.data.api.dto.WhitelistDto>> = handleApiCall {
                client.get(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                    header("apikey", ApiConfig.ANON_KEY)
                    header("Authorization", "Bearer ${window.localStorage.getItem("access_token")}")
                }
            }
            when (result) {
                is Result.Success -> {
                    whitelist = result.data
                    error = null
                }
                is Result.Error -> error = result.message
                else -> {}
            }
            isLoading = false
        }
    }

    fun addToWhitelist() {
        if (newEmail.isBlank() && newSicilNo.isBlank()) return
        scope.launch {
            isAdding = true
            val response = client.post(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                header("apikey", ApiConfig.ANON_KEY)
                header("Authorization", "Bearer ${window.localStorage.getItem("access_token")}")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(buildMap {
                    if (newEmail.isNotBlank()) put("email", newEmail)
                    if (newSicilNo.isNotBlank()) put("sicil_no", newSicilNo)
                })
            }
            if (response.status.isSuccess()) {
                newEmail = ""
                newSicilNo = ""
                loadWhitelist()
            } else {
                error = "Ekleme başarısız: ${response.status}"
            }
            isAdding = false
        }
    }

    fun deleteFromWhitelist(id: String) {
        scope.launch {
            val response = client.delete(ApiConfig.BASE_URL + "/rest/v1/whitelist") {
                parameter("id", "eq.$id")
                header("apikey", ApiConfig.ANON_KEY)
                header("Authorization", "Bearer ${window.localStorage.getItem("access_token")}")
            }
            if (response.status.isSuccess()) {
                loadWhitelist()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadWhitelist()
    }

    Div({
        style {
            maxWidth(1200.px)
            property("margin", "0 auto")
            padding(40.px)
        }
    }) {
        H1 { Text("🛡️ Admin Paneli") }
        P({ style { color(Color("#64748B")); marginBottom(32.px) } }) {
            Text("Kullanıcı erişim listesini (whitelist) buradan yönetebilirsiniz.")
        }

        // Add New section
        Div({
            style {
                backgroundColor(Color.white)
                padding(24.px)
                borderRadius(12.px)
                property("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
                marginBottom(32.px)
                display(DisplayStyle.Flex)
                gap(16.px)
                alignItems(AlignItems.FlexEnd)
            }
        }) {
            Div({ style { flex(1) } }) {
                Label(attrs = { 
                    style { 
                        property("display", "block")
                        property("margin-bottom", "8px")
                        property("font-weight", "600") 
                    } 
                }) { Text("E-posta (Opsiyonel)") }
                Input(InputType.Email, attrs = {
                    value(newEmail)
                    onInput { newEmail = it.value }
                    placeholder("email@example.com")
                    style {
                        width(100.percent); padding(10.px); borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                    }
                })
            }
            Div({ style { flex(1) } }) {
                Label(attrs = { 
                    style { 
                        property("display", "block")
                        property("margin-bottom", "8px")
                        property("font-weight", "600") 
                    } 
                }) { Text("Sicil No") }
                Input(InputType.Text, attrs = {
                    value(newSicilNo)
                    onInput { newSicilNo = it.value }
                    placeholder("12345")
                    style {
                        width(100.percent); padding(10.px); borderRadius(6.px)
                        border(1.px, LineStyle.Solid, Color("#E2E8F0"))
                    }
                })
            }
            Button({
                style {
                    padding(10.px, 24.px); backgroundColor(Color("#3B82F6")); color(Color.white)
                    border(0.px); borderRadius(6.px); cursor("pointer"); fontWeight(600)
                }
                onClick { addToWhitelist() }
                if (isAdding) disabled()
            }) {
                Text(if (isAdding) "Ekleniyor..." else "Listeye Ekle")
            }
        }

        if (error != null) {
            Div({
                style {
                    padding(16.px); backgroundColor(Color("#FEE2E2")); color(Color("#B91C1C"))
                    borderRadius(8.px); marginBottom(24.px)
                }
            }) { Text(error!!) }
        }

        // Whitelist Table
        Div({
            style {
                backgroundColor(Color.white)
                borderRadius(12.px)
                property("box-shadow", "0 1px 3px rgba(0,0,0,0.1)")
                overflow("hidden")
            }
        }) {
            Table({
                style { width(100.percent); property("border-collapse", "collapse") }
            }) {
                Thead {
                    Tr({ style { backgroundColor(Color("#F8FAFC")); textAlign("left") } }) {
                        Th({ style { padding(16.px); property("border-bottom", "1px solid #E2E8F0") } }) { Text("Düzenleme") }
                        Th({ style { padding(16.px); property("border-bottom", "1px solid #E2E8F0") } }) { Text("E-posta") }
                        Th({ style { padding(16.px); property("border-bottom", "1px solid #E2E8F0") } }) { Text("Sicil No") }
                        Th({ style { padding(16.px); property("border-bottom", "1px solid #E2E8F0") } }) { Text("İşlem") }
                    }
                }
                Tbody {
                    if (isLoading) {
                        Tr { Td(attrs = { attr("colspan", "4"); style { padding(32.px); textAlign("center") } }) { Text("Yükleniyor...") } }
                    } else if (whitelist.isEmpty()) {
                        Tr { Td(attrs = { attr("colspan", "4"); style { padding(32.px); textAlign("center") } }) { Text("Liste boş.") } }
                    } else {
                        whitelist.forEach { item ->
                            Tr({ style { property("border-bottom", "1px solid #F1F5F9") } }) {
                                Td({ style { padding(16.px) } }) { 
                                    Text(item.added_at?.take(10) ?: "-") 
                                }
                                Td({ style { padding(16.px) } }) { Text(item.email ?: "-") }
                                Td({ style { padding(16.px); fontWeight(600) } }) { Text(item.sicil_no ?: "-") }
                                Td({ style { padding(16.px) } }) {
                                    Button({
                                        style {
                                            color(Color("#EF4444")); backgroundColor(Color.transparent)
                                            border(0.px); cursor("pointer"); fontWeight(600)
                                        }
                                        onClick { 
                                            if (window.confirm("Bu kullanıcıyı listeden çıkarmak istediğinize emin misiniz?")) {
                                                deleteFromWhitelist(item.id ?: "")
                                            }
                                        }
                                    }) { Text("Kaldır") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
