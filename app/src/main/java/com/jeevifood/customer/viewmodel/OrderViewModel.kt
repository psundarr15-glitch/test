package com.jeevifood.customer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevifood.customer.data.CheckoutRequest
import com.jeevifood.customer.data.Order
import com.jeevifood.customer.repository.CartRepository
import com.jeevifood.customer.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepo: OrderRepository,
    private val cartRepo: CartRepository
) : ViewModel() {

    var checkoutState by mutableStateOf<UiState<Order>>(UiState.Idle)
        private set

    var orders by mutableStateOf<UiState<List<Order>>>(UiState.Idle)
        private set

    var orderDetail by mutableStateOf<UiState<Order>>(UiState.Idle)
        private set

    fun placeOrder(
        addressId: Int,
        paymentMethod: String,
        couponCode: String?,
        instructions: String?
    ) {
        checkoutState = UiState.Loading
        viewModelScope.launch {
            orderRepo.placeOrder(
                CheckoutRequest(
                    address_id = addressId,
                    payment_method = paymentMethod,
                    coupon_code = couponCode,
                    delivery_instructions = instructions
                )
            ).onSuccess {
                checkoutState = UiState.Success(it)
                cartRepo.clear() // server-side cart cleared after successful order
            }.onFailure {
                checkoutState = UiState.Error(it.message ?: "Order-ஐ செய்ய முடியவில்லை")
            }
        }
    }

    fun loadOrders() {
        orders = UiState.Loading
        viewModelScope.launch {
            orderRepo.myOrders()
                .onSuccess { orders = UiState.Success(it.data) }
                .onFailure { orders = UiState.Error(it.message ?: "Could not load orders") }
        }
    }

    fun loadOrderDetail(id: Int) {
        orderDetail = UiState.Loading
        viewModelScope.launch {
            orderRepo.orderDetail(id)
                .onSuccess { orderDetail = UiState.Success(it) }
                .onFailure { orderDetail = UiState.Error(it.message ?: "Could not load order") }
        }
    }

    fun cancelOrder(id: Int, reason: String, onDone: () -> Unit) {
        viewModelScope.launch {
            orderRepo.cancelOrder(id, reason).onSuccess {
                loadOrderDetail(id)
                onDone()
            }
        }
    }

    fun resetCheckout() {
        checkoutState = UiState.Idle
    }
}
