package com.jeevifood.customer.network

import com.jeevifood.customer.BuildConfig
import com.jeevifood.customer.data.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    /**
     * Base URL for the Laravel backend, e.g. "https://your-domain.com/api/v1/".
     * Set via BuildConfig.API_BASE_URL in app/build.gradle.kts — change the
     * placeholder there to your live server URL before building.
     *
     * Testing against a Laravel dev server running on your own PC from the
     * Android emulator? Use "http://10.0.2.2:<port>/api/v1/" — 10.0.2.2 is
     * the emulator's alias for your machine's localhost.
     */
    private const val BASE_URL_FALLBACK = "https://test.tvkomalur.xyz/api/v1/"

    fun create(tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        val baseUrl = BuildConfig.API_BASE_URL.ifBlank { BASE_URL_FALLBACK }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
