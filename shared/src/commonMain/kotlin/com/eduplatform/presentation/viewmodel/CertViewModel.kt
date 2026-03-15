package com.eduplatform.presentation.viewmodel

import com.eduplatform.domain.model.Certificate
import com.eduplatform.domain.repository.CertificateRepository
import com.eduplatform.util.onError
import com.eduplatform.util.onSuccess

data class CertState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val certificates: List<Certificate> = emptyList(),
    val selected: Certificate? = null,
    val pdfGenerating: Boolean = false,
    val pdfUrl: String? = null
)

sealed class CertIntent {
    data class Load(val userId: String) : CertIntent()
    data class Select(val cert: Certificate) : CertIntent()
    data class GeneratePdf(val certId: String) : CertIntent()
    data class Verify(val code: String) : CertIntent()
    object ClearError : CertIntent()
}

class CertViewModel(private val repository: CertificateRepository) : BaseViewModel<CertState, CertIntent>(CertState()) {

    override suspend fun handleIntent(intent: CertIntent) {
        when (intent) {
            is CertIntent.Load -> {
                setState { copy(isLoading = true, error = null) }
                repository.getMyCertificates(intent.userId)
                    .onSuccess { list -> setState { copy(isLoading = false, certificates = list) } }
                    .onError { message -> setState { copy(isLoading = false, error = message) } }
            }
            is CertIntent.Select -> {
                setState { copy(selected = intent.cert, pdfUrl = null) }
            }
            is CertIntent.GeneratePdf -> {
                setState { copy(pdfGenerating = true, error = null) }
                repository.generatePdf(intent.certId)
                    .onSuccess { url -> setState { copy(pdfGenerating = false, pdfUrl = url) } }
                    .onError { message -> setState { copy(error = message, pdfGenerating = false) } }
            }
            is CertIntent.Verify -> {
                setState { copy(isLoading = true, error = null) }
                repository.verifyCertificate(intent.code)
                    .onSuccess { cert ->
                        if (cert != null) {
                            setState { copy(isLoading = false, selected = cert) }
                        } else {
                            setState { copy(isLoading = false, error = "Sertifika bulunamadı.") }
                        }
                    }
                    .onError { message -> setState { copy(isLoading = false, error = message) } }
            }
            is CertIntent.ClearError -> setState { copy(error = null) }
        }
    }
}
