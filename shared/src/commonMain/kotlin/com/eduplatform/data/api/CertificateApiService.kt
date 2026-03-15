package com.eduplatform.data.api

import com.eduplatform.data.api.dto.CertificateDto
import com.eduplatform.util.Result
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class CertificateApiService(private val client: HttpClient) {

    suspend fun getMyCerts(userId: String): Result<List<CertificateDto>> {
        return handleApiCall {
            client.get(ApiConfig.BASE_URL + ApiConfig.CERTIFICATES) {
                parameter("user_id", "eq.$userId")
                parameter("order", "issued_at.desc")
            }
        }
    }

    suspend fun verify(code: String): Result<CertificateDto?> {
        // Verification often doesn't require auth, but use handleApiCall for consistency
        return handleApiCall<List<CertificateDto>> {
            client.get(ApiConfig.BASE_URL + ApiConfig.CERTIFICATES) {
                parameter("verify_code", "eq.$code")
            }
        }.let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.firstOrNull())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    suspend fun generatePdf(certId: String): Result<String> {
        return handleApiCall<Map<String, String>> {
            client.post(ApiConfig.BASE_URL + ApiConfig.GEN_PDF) {
                setBody(mapOf("certificateId" to certId))
            }
        }.let { result ->
            when (result) {
                is Result.Success -> {
                    val url = result.data["pdf_url"]
                    if (url != null) Result.Success(url)
                    else Result.Error("PDF oluşturulamadı.")
                }
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }
}
