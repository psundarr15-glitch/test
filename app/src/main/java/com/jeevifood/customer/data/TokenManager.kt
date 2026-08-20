package com.jeevifood.customer.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "jeevi_food_prefs")

/**
 * Persists the Sanctum bearer token + minimal user info across app restarts.
 * A tiny in-memory cache lets the OkHttp interceptor read the token
 * synchronously (interceptors can't suspend).
 */
class TokenManager(private val context: Context) {

    private val tokenKey = stringPreferencesKey("auth_token")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userPhoneKey = stringPreferencesKey("user_phone")

    @Volatile
    var cachedToken: String? = null
        private set

    init {
        // Warm the in-memory cache once at process start.
        runBlocking { cachedToken = context.dataStore.data.map { it[tokenKey] }.first() }
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[tokenKey] }

    suspend fun saveSession(token: String, userName: String, userPhone: String) {
        cachedToken = token
        context.dataStore.edit {
            it[tokenKey] = token
            it[userNameKey] = userName
            it[userPhoneKey] = userPhone
        }
    }

    suspend fun clearSession() {
        cachedToken = null
        context.dataStore.edit { it.clear() }
    }

    suspend fun isLoggedIn(): Boolean = tokenFlow.first() != null
}
