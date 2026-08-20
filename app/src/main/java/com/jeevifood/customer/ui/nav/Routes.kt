package com.jeevifood.customer.ui.nav

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{phone}"
    fun otp(phone: String) = "otp/$phone"

    const val HOME = "home"
    const val RESTAURANT_MENU = "restaurant/{restaurantId}"
    fun restaurantMenu(id: Int) = "restaurant/$id"

    const val FOOD_DETAIL = "food/{foodId}"
    fun foodDetail(id: Int) = "food/$id"

    const val CART = "cart"
    const val ADDRESSES = "addresses"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"

    const val ORDER_DETAIL = "orders/{orderId}"
    fun orderDetail(id: Int) = "orders/$id"

    const val PROFILE = "profile"
}
