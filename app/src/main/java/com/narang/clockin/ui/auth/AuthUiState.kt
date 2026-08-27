package com.narang.clockin.ui.auth

/**
 * Represents the UI state for the Authentication screens (Login/Signup).
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
