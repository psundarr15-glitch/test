package com.jeevifood.customer.repository

import com.jeevifood.customer.data.*
import com.jeevifood.customer.network.ApiService

class CatalogRepository(private val api: ApiService) {

    suspend fun categories(): Result<List<Category>> = safeApiCall { api.categories() }

    suspend fun restaurants(search: String? = null, zoneId: Int? = null, featured: Boolean = false): Result<Paginated<Restaurant>> =
        safeApiCall { api.restaurants(zoneId = zoneId, search = search, featured = if (featured) 1 else null) }

    suspend fun restaurantDetail(id: Int): Result<Restaurant> = safeApiCall { api.restaurantDetail(id) }

    suspend fun restaurantMenu(id: Int): Result<Map<String, List<FoodItem>>> = safeApiCall { api.restaurantMenu(id) }

    suspend fun searchFoodItems(query: String): Result<Paginated<FoodItem>> =
        safeApiCall { api.foodItems(search = query) }

    suspend fun foodItemDetail(id: Int): Result<FoodItem> = safeApiCall { api.foodItemDetail(id) }
}
