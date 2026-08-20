package com.jeevifood.customer.network

import com.jeevifood.customer.data.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/** Adds `Authorization: Bearer <token>` + Accept header to every outgoing request. */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder().addHeader("Accept", "application/json")

        tokenManager.cachedToken?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }
}
