package com.narang.clockin.domain

data class AuthUser(
    val id: String,
    val email: String,
    val token: String? = null
)
