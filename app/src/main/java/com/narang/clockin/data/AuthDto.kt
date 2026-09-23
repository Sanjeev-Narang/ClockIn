package com.narang.clockin.data

import com.narang.clockin.domain.AuthUser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SignupRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "id") val id: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "token") val token: String? = null
)

fun AuthResponseDto.toDomain(fallbackEmail: String): AuthUser = AuthUser(
    id = id ?: "",
    email = email ?: fallbackEmail,
    token = token
)
