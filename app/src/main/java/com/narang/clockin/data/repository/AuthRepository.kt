package com.narang.clockin.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser?>
    suspend fun signup(email: String, password: String): Result<FirebaseUser?>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}
