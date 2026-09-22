package com.narang.clockin.ui.auth

/**
 * UI states for the Authentication screens (Login/Signup).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
