package com.jeevifood.customer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.jeevifood.customer.JeeviFoodApplication
import com.jeevifood.customer.repository.*

/**
 * Simple hand-rolled factory (no Hilt/Dagger) so the project builds with
 * fewer moving parts. Each ViewModel receives the repositories it needs,
 * all built from the single ApiService living in JeeviFoodApplication.
 */
class ViewModelFactory(private val app: JeeviFoodApplication) : ViewModelProvider.Factory {

    private val authRepository by lazy { AuthRepository(app.apiService, app.tokenManager) }
    private val catalogRepository by lazy { CatalogRepository(app.apiService) }
    private val cartRepository by lazy { CartRepository(app.apiService) }
    private val addressRepository by lazy { AddressRepository(app.apiService) }
    private val orderRepository by lazy { OrderRepository(app.apiService) }

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        @Suppress("UNCHECKED_CAST")
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(authRepository) as T
            CatalogViewModel::class.java -> CatalogViewModel(catalogRepository) as T
            CartViewModel::class.java -> CartViewModel(cartRepository) as T
            AddressViewModel::class.java -> AddressViewModel(addressRepository) as T
            OrderViewModel::class.java -> OrderViewModel(orderRepository, cartRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
