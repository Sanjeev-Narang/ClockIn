package com.narang.clockin.domain

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun signup(email: String, password: String): Result<AuthUser>
    fun logout()
    fun getCurrentUser(): AuthUser?
}
