package com.jeevifood.customer.repository

import com.jeevifood.customer.data.*
import com.jeevifood.customer.network.ApiService

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun register(
        fName: String, lName: String?, phone: String, email: String?,
        password: String, passwordConfirmation: String, referralCode: String?
    ): Result<RegisterData> = safeApiCall {
        api.register(RegisterRequest(fName, lName, phone, email, password, passwordConfirmation, referralCode))
    }

    suspend fun login(phone: String, password: String): Result<LoginData> {
        val result = safeApiCall { api.login(LoginRequest(phone, password)) }
        result.onSuccess { data ->
            tokenManager.saveSession(data.token, data.user.fName, data.user.phone)
        }
        return result
    }

    suspend fun sendOtp(identifier: String): Result<Unit> = safeApiCall {
        api.sendOtp(OtpSendRequest(identifier))
    }

    suspend fun verifyOtp(identifier: String, otp: String): Result<Unit> = safeApiCall {
        api.verifyOtp(OtpVerifyRequest(identifier, otp))
    }

    suspend fun resetPassword(identifier: String, password: String, confirmation: String): Result<Unit> =
        safeApiCall { api.resetPassword(ResetPasswordRequest(identifier, password, confirmation)) }

    suspend fun me(): Result<User> = safeApiCall { api.me() }

    suspend fun logout() {
        runCatching { api.logout() }
        tokenManager.clearSession()
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
}
