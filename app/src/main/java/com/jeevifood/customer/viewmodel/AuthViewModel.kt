package com.jeevifood.customer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevifood.customer.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    var loginState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    var registerState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    var otpState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    var pendingPhone by mutableStateOf("")
        private set

    fun login(phone: String, password: String) {
        loginState = UiState.Loading
        viewModelScope.launch {
            repo.login(phone, password)
                .onSuccess { loginState = UiState.Success(Unit) }
                .onFailure { loginState = UiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(fName: String, lName: String?, phone: String, email: String?, password: String, confirm: String) {
        registerState = UiState.Loading
        pendingPhone = phone
        viewModelScope.launch {
            repo.register(fName, lName, phone, email, password, confirm, null)
                .onSuccess { registerState = UiState.Success(Unit) }
                .onFailure { registerState = UiState.Error(it.message ?: "Registration failed") }
        }
    }

    fun sendOtp(identifier: String) {
        otpState = UiState.Loading
        viewModelScope.launch {
            repo.sendOtp(identifier)
                .onSuccess { otpState = UiState.Success(Unit) }
                .onFailure { otpState = UiState.Error(it.message ?: "Could not send OTP") }
        }
    }

    fun verifyOtp(identifier: String, otp: String, onVerified: () -> Unit) {
        otpState = UiState.Loading
        viewModelScope.launch {
            repo.verifyOtp(identifier, otp)
                .onSuccess {
                    otpState = UiState.Success(Unit)
                    onVerified()
                }
                .onFailure { otpState = UiState.Error(it.message ?: "Invalid OTP") }
        }
    }

    fun resetLoginState() {
        loginState = UiState.Idle
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.logout()
            onDone()
        }
    }
}
