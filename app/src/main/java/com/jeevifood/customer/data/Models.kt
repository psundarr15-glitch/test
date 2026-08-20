package com.jeevifood.customer.data

import com.google.gson.annotations.SerializedName

// ---------- Generic API envelope ----------
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    @SerializedName("error_code") val errorCode: String? = null
)

// Laravel paginate() wraps lists like { data: [...], current_page, last_page, total, ... }
data class Paginated<T>(
    val data: List<T> = emptyList(),
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("last_page") val lastPage: Int = 1,
    val total: Int = 0
)

// ---------- Auth ----------
data class User(
    val id: Int,
    @SerializedName("f_name") val fName: String,
    @SerializedName("l_name") val lName: String?,
    val email: String?,
    val phone: String,
    val image: String?,
    @SerializedName("is_phone_verified") val isPhoneVerified: Boolean = false,
    val status: String = "active"
)

data class LoginRequest(val phone: String, val password: String, val fcm_token: String? = null)

data class RegisterRequest(
    val f_name: String,
    val l_name: String? = null,
    val phone: String,
    val email: String? = null,
    val password: String,
    val password_confirmation: String,
    val referral_code: String? = null
)

data class LoginData(val token: String, val user: User)
data class RegisterData(@SerializedName("user_id") val userId: Int)

data class OtpSendRequest(val phone_or_email: String)
data class OtpVerifyRequest(val phone_or_email: String, val otp: String)
data class ResetPasswordRequest(
    val phone_or_email: String,
    val password: String,
    val password_confirmation: String
)

// ---------- Catalog ----------
data class Category(
    val id: Int,
    val name: String,
    val image: String?,
    val children: List<Category>? = null
)

data class Zone(val id: Int, val name: String)

data class Restaurant(
    val id: Int,
    val name: String,
    val logo: String?,
    @SerializedName("cover_photo") val coverPhoto: String?,
    val address: String?,
    @SerializedName("delivery_time") val deliveryTime: String?,
    @SerializedName("minimum_order") val minimumOrder: Double = 0.0,
    @SerializedName("avg_rating") val avgRating: Double = 0.0,
    @SerializedName("is_featured") val isFeatured: Boolean = false,
    @SerializedName("is_open_now") val isOpenNow: Boolean = true,
    @SerializedName("tax_rate") val taxRate: Double = 0.0,
    val zone: Zone? = null,
    @SerializedName("food_items_count") val foodItemsCount: Int? = null
)

data class VariationOption(
    val id: Int,
    val label: String,
    @SerializedName("additional_price") val additionalPrice: Double = 0.0
)

data class ItemVariation(
    val id: Int,
    val name: String,
    @SerializedName("is_required") val isRequired: Boolean = false,
    @SerializedName("max_selectable") val maxSelectable: Int = 1,
    val options: List<VariationOption> = emptyList()
)

data class Addon(
    val id: Int,
    val name: String,
    val price: Double
)

data class FoodItem(
    val id: Int,
    @SerializedName("restaurant_id") val restaurantId: Int,
    val name: String,
    val description: String?,
    val image: String?,
    val price: Double,
    @SerializedName("discount_price") val discountPrice: Double?,
    @SerializedName("is_veg") val isVeg: Boolean = true,
    @SerializedName("is_available") val isAvailable: Boolean = true,
    @SerializedName("avg_rating") val avgRating: Double = 0.0,
    val restaurant: Restaurant? = null,
    val category: Category? = null,
    val variations: List<ItemVariation> = emptyList(),
    val addons: List<Addon> = emptyList()
)

// ---------- Cart ----------
data class AddToCartRequest(
    val food_item_id: Int,
    val quantity: Int,
    val variation_option_ids: List<Int> = emptyList(),
    val addon_ids: List<Int> = emptyList()
)

data class UpdateCartRequest(val quantity: Int)

data class CartItem(
    val id: Int,
    @SerializedName("food_item_id") val foodItemId: Int,
    @SerializedName("restaurant_id") val restaurantId: Int,
    val quantity: Int,
    @SerializedName("item_price") val itemPrice: Double,
    @SerializedName("foodItem") val foodItem: FoodItem? = null,
    val subtotal: Double = 0.0
)

data class CartData(
    val items: List<CartItem> = emptyList(),
    @SerializedName("item_total") val itemTotal: Double = 0.0,
    @SerializedName("restaurant_id") val restaurantId: Int? = null
)

// ---------- Address ----------
data class Address(
    val id: Int? = null,
    val address_type: String = "home",
    val contact_person_name: String? = null,
    val contact_person_number: String? = null,
    val address_line: String,
    val landmark: String? = null,
    val city: String? = null,
    val latitude: Double,
    val longitude: Double,
    val is_default: Boolean = false
)

// ---------- Coupon ----------
data class ApplyCouponRequest(val code: String)
data class CouponResult(
    val code: String,
    val title: String? = null,
    @SerializedName("discount_amount") val discountAmount: Double,
    @SerializedName("item_total") val itemTotal: Double,
    @SerializedName("payable_total") val payableTotal: Double
)

// ---------- Orders ----------
data class CheckoutRequest(
    val address_id: Int,
    val payment_method: String, // cod | online | wallet
    val order_type: String = "delivery",
    val coupon_code: String? = null,
    val tip_amount: Double? = null,
    val delivery_instructions: String? = null,
    val scheduled_at: String? = null
)

data class OrderDetail(
    val id: Int,
    @SerializedName("item_name") val itemName: String,
    val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Double,
    val subtotal: Double
)

data class OrderStatusHistory(
    val status: String,
    @SerializedName("changed_by") val changedBy: String,
    @SerializedName("created_at") val createdAt: String? = null
)

data class Order(
    val id: Int,
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("order_status") val orderStatus: String,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("item_total") val itemTotal: Double,
    @SerializedName("discount_amount") val discountAmount: Double = 0.0,
    @SerializedName("delivery_fee") val deliveryFee: Double = 0.0,
    @SerializedName("tax_amount") val taxAmount: Double = 0.0,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("payment_status") val paymentStatus: String,
    val restaurant: Restaurant? = null,
    val details: List<OrderDetail>? = null,
    @SerializedName("statusHistory") val statusHistory: List<OrderStatusHistory>? = null,
    @SerializedName("created_at") val createdAt: String? = null
)
