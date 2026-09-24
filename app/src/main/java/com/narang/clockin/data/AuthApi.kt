package com.narang.clockin.data

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): AuthResponse
}
