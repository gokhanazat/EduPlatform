package com.eduplatform.data.repository

import com.eduplatform.data.api.CertificateApiService
import com.eduplatform.domain.model.Certificate
import com.eduplatform.domain.repository.CertificateRepository
import com.eduplatform.util.Result

class CertificateRepositoryImpl(
    private val api: CertificateApiService
) : CertificateRepository {

    override suspend fun getMyCertificates(userId: String): Result<List<Certificate>> {
        return api.getMyCerts(userId).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun verifyCertificate(code: String): Result<Certificate?> {
        return api.verify(code).let { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data?.toDomain())
                is Result.Error -> result
                is Result.Loading -> result
            }
        }
    }

    override suspend fun generatePdf(certId: String): Result<String> {
        return api.generatePdf(certId)
    }
}
