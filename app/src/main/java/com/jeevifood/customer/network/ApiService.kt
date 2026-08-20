package com.jeevifood.customer.network

import com.jeevifood.customer.data.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Maps 1:1 to routes/api.php (prefix /api/v1) in the Laravel backend.
 * Every endpoint under auth:sanctum needs the "Authorization: Bearer <token>"
 * header — AuthInterceptor (network/AuthInterceptor.kt) attaches it automatically.
 */
interface ApiService {

    // ---------- Auth (public) ----------
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<ApiResponse<RegisterData>>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<ApiResponse<LoginData>>

    @POST("auth/otp/send")
    suspend fun sendOtp(@Body body: OtpSendRequest): Response<ApiResponse<Unit>>

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body body: OtpVerifyRequest): Response<ApiResponse<Unit>>

    @POST("auth/password/reset")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<ApiResponse<Unit>>

    // ---------- Auth (authenticated) ----------
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("auth/me")
    suspend fun me(): Response<ApiResponse<User>>

    // ---------- Catalog (public) ----------
    @GET("categories")
    suspend fun categories(): Response<ApiResponse<List<Category>>>

    @GET("restaurants")
    suspend fun restaurants(
        @Query("zone_id") zoneId: Int? = null,
        @Query("search") search: String? = null,
        @Query("featured") featured: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("per_page") perPage: Int = 15
    ): Response<ApiResponse<Paginated<Restaurant>>>

    @GET("restaurants/{id}")
    suspend fun restaurantDetail(@Path("id") id: Int): Response<ApiResponse<Restaurant>>

    @GET("restaurants/{id}/menu")
    suspend fun restaurantMenu(@Path("id") id: Int): Response<ApiResponse<Map<String, List<FoodItem>>>>

    @GET("food-items")
    suspend fun foodItems(
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("restaurant_id") restaurantId: Int? = null,
        @Query("is_veg") isVeg: Boolean? = null,
        @Query("per_page") perPage: Int = 20
    ): Response<ApiResponse<Paginated<FoodItem>>>

    @GET("food-items/{id}")
    suspend fun foodItemDetail(@Path("id") id: Int): Response<ApiResponse<FoodItem>>

    // ---------- Addresses (authenticated) ----------
    @GET("addresses")
    suspend fun addresses(): Response<ApiResponse<List<Address>>>

    @POST("addresses")
    suspend fun addAddress(@Body body: Address): Response<ApiResponse<Address>>

    @PUT("addresses/{id}")
    suspend fun updateAddress(@Path("id") id: Int, @Body body: Address): Response<ApiResponse<Address>>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: Int): Response<ApiResponse<Unit>>

    // ---------- Cart (authenticated) ----------
    @GET("cart")
    suspend fun cart(): Response<ApiResponse<CartData>>

    @POST("cart")
    suspend fun addToCart(@Body body: AddToCartRequest): Response<ApiResponse<CartItem>>

    @PATCH("cart/{id}")
    suspend fun updateCartItem(@Path("id") id: Int, @Body body: UpdateCartRequest): Response<ApiResponse<CartItem>>

    @DELETE("cart/{id}")
    suspend fun removeCartItem(@Path("id") id: Int): Response<ApiResponse<Unit>>

    @DELETE("cart")
    suspend fun clearCart(): Response<ApiResponse<Unit>>

    // ---------- Coupons (authenticated) ----------
    @POST("coupons/apply")
    suspend fun applyCoupon(@Body body: ApplyCouponRequest): Response<ApiResponse<CouponResult>>

    // ---------- Orders (authenticated) ----------
    @GET("orders")
    suspend fun orders(@Query("per_page") perPage: Int = 10): Response<ApiResponse<Paginated<Order>>>

    @POST("orders")
    suspend fun placeOrder(@Body body: CheckoutRequest): Response<ApiResponse<Order>>

    @GET("orders/{id}")
    suspend fun orderDetail(@Path("id") id: Int): Response<ApiResponse<Order>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Int, @Body body: Map<String, String>): Response<ApiResponse<Order>>
}
