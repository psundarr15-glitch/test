package com.jeevifood.customer

import android.app.Application
import com.jeevifood.customer.data.TokenManager
import com.jeevifood.customer.network.ApiClient
import com.jeevifood.customer.network.ApiService

class JeeviFoodApplication : Application() {

    lateinit var tokenManager: TokenManager
        private set

    lateinit var apiService: ApiService
        private set

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
        apiService = ApiClient.create(tokenManager)
    }
}

