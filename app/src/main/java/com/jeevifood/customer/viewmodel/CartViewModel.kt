package com.jeevifood.customer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevifood.customer.data.CartData
import com.jeevifood.customer.data.CouponResult
import com.jeevifood.customer.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel(private val repo: CartRepository) : ViewModel() {

    var cart by mutableStateOf<UiState<CartData>>(UiState.Idle)
        private set

    var appliedCoupon by mutableStateOf<CouponResult?>(null)
        private set

    var actionMessage by mutableStateOf<String?>(null)
        private set

    fun loadCart() {
        cart = UiState.Loading
        viewModelScope.launch {
            repo.cart()
                .onSuccess { cart = UiState.Success(it) }
                .onFailure { cart = UiState.Error(it.message ?: "Could not load cart") }
        }
    }

    fun addItem(foodItemId: Int, quantity: Int, variationOptionIds: List<Int> = emptyList(), addonIds: List<Int> = emptyList()) {
        viewModelScope.launch {
            repo.add(foodItemId, quantity, variationOptionIds, addonIds)
                .onSuccess {
                    actionMessage = "Cart-க்கு சேர்க்கப்பட்டது"
                    loadCart()
                }
                .onFailure { actionMessage = it.message }
        }
    }

    fun updateQuantity(cartItemId: Int, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                repo.remove(cartItemId).onSuccess { loadCart() }
            } else {
                repo.updateQuantity(cartItemId, quantity).onSuccess { loadCart() }
            }
        }
    }

    fun removeItem(cartItemId: Int) {
        viewModelScope.launch {
            repo.remove(cartItemId).onSuccess { loadCart() }
        }
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            repo.applyCoupon(code)
                .onSuccess { appliedCoupon = it }
                .onFailure { actionMessage = it.message }
        }
    }

    fun clearCoupon() {
        appliedCoupon = null
    }

    fun clearCartAfterOrder() {
        appliedCoupon = null
        cart = UiState.Idle
    }

    fun consumeMessage() {
        actionMessage = null
    }
}
