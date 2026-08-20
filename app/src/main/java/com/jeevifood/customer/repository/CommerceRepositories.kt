package com.jeevifood.customer.repository

import com.jeevifood.customer.data.*
import com.jeevifood.customer.network.ApiService

class CartRepository(private val api: ApiService) {
    suspend fun cart(): Result<CartData> = safeApiCall { api.cart() }

    suspend fun add(foodItemId: Int, quantity: Int, variationOptionIds: List<Int>, addonIds: List<Int>): Result<CartItem> =
        safeApiCall { api.addToCart(AddToCartRequest(foodItemId, quantity, variationOptionIds, addonIds)) }

    suspend fun updateQuantity(cartItemId: Int, quantity: Int): Result<CartItem> =
        safeApiCall { api.updateCartItem(cartItemId, UpdateCartRequest(quantity)) }

    suspend fun remove(cartItemId: Int): Result<Unit> = safeApiCall { api.removeCartItem(cartItemId) }

    suspend fun clear(): Result<Unit> = safeApiCall { api.clearCart() }

    suspend fun applyCoupon(code: String): Result<CouponResult> = safeApiCall { api.applyCoupon(ApplyCouponRequest(code)) }
}

class AddressRepository(private val api: ApiService) {
    suspend fun list(): Result<List<Address>> = safeApiCall { api.addresses() }

    suspend fun add(address: Address): Result<Address> = safeApiCall { api.addAddress(address) }

    suspend fun update(id: Int, address: Address): Result<Address> = safeApiCall { api.updateAddress(id, address) }

    suspend fun delete(id: Int): Result<Unit> = safeApiCall { api.deleteAddress(id) }
}

class OrderRepository(private val api: ApiService) {
    suspend fun placeOrder(request: CheckoutRequest): Result<Order> = safeApiCall { api.placeOrder(request) }

    suspend fun myOrders(): Result<Paginated<Order>> = safeApiCall { api.orders() }

    suspend fun orderDetail(id: Int): Result<Order> = safeApiCall { api.orderDetail(id) }

    suspend fun cancelOrder(id: Int, reason: String?): Result<Order> =
        safeApiCall { api.cancelOrder(id, if (reason != null) mapOf("reason" to reason) else emptyMap()) }
}
