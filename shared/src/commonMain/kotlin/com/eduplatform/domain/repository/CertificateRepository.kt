package com.eduplatform.domain.repository

import com.eduplatform.domain.model.Certificate
import com.eduplatform.util.Result

interface CertificateRepository {
    suspend fun getMyCertificates(userId: String): Result<List<Certificate>>
    suspend fun verifyCertificate(code: String): Result<Certificate?>
    suspend fun generatePdf(certId: String): Result<String>
}
