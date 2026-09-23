package com.narang.clockin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.narang.clockin.domain.AuthRepository
import com.narang.clockin.domain.Result
import com.narang.clockin.ui.auth.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            Timber.d("AuthViewModel: logging in")
            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    Timber.d("Login success: %s", result.data.email)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    Timber.e("Login error: %s", result.message)
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun signup(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            Timber.d("AuthViewModel: signing up")
            when (val result = repository.signup(email, password)) {
                is Result.Success -> {
                    Timber.d("Signup success: %s", result.data.email)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Result.Error -> {
                    Timber.e("Signup error: %s", result.message)
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Result.Loading -> Unit
            }
        }
    }
}
