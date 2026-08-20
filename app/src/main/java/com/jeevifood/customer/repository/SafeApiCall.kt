package com.jeevifood.customer.repository

import com.jeevifood.customer.data.ApiResponse
import retrofit2.Response
import java.io.IOException

/**
 * Wraps a Retrofit call to a Laravel endpoint. Every controller in this
 * backend returns { success, message, data }, so this unwraps that shape
 * and turns success=false / HTTP errors / network errors into a single
 * Kotlin Result the UI layer can handle uniformly.
 */
suspend fun <T> safeApiCall(block: suspend () -> Response<ApiResponse<T>>): Result<T> {
    return try {
        val response = block()
        val body = response.body()

        if (response.isSuccessful && body?.success == true) {
            @Suppress("UNCHECKED_CAST")
            Result.success((body.data ?: Unit) as T)
        } else {
            val errorMsg = body?.message
                ?: parseErrorBody(response)
                ?: "Something went wrong (HTTP ${response.code()})."
            Result.failure(ApiException(errorMsg, body?.errorCode))
        }
    } catch (e: IOException) {
        Result.failure(ApiException("Network error. Please check your internet connection."))
    } catch (e: Exception) {
        Result.failure(ApiException(e.message ?: "Unexpected error."))
    }
}

private fun parseErrorBody(response: Response<*>): String? {
    return try {
        response.errorBody()?.string()
    } catch (e: Exception) {
        null
    }
}

class ApiException(message: String, val errorCode: String? = null) : Exception(message)
