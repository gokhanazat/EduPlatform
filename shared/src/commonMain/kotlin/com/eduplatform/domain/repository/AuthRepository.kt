package com.eduplatform.domain.repository

import com.eduplatform.data.api.dto.WhitelistDto
import com.eduplatform.domain.model.User
import com.eduplatform.util.Result

interface AuthRepository {
    suspend fun signUp(email: String, password: String, fullName: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun isLoggedIn(): Boolean

    // --- ADMIN İŞLEMLERİ ---
    suspend fun getWhitelist(search: String? = null): Result<List<WhitelistDto>>
    suspend fun addToWhitelist(email: String?, sicilNo: String?, city: String, notes: String = ""): Result<Unit>
    suspend fun updateWhitelist(id: String, updates: Map<String, Any>): Result<Unit>
    suspend fun removeFromWhitelist(id: String): Result<Unit>
}
