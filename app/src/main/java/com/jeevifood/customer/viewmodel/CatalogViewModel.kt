package com.jeevifood.customer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevifood.customer.data.Category
import com.jeevifood.customer.data.FoodItem
import com.jeevifood.customer.data.Restaurant
import com.jeevifood.customer.repository.CatalogRepository
import kotlinx.coroutines.launch

class CatalogViewModel(private val repo: CatalogRepository) : ViewModel() {

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var restaurants by mutableStateOf<UiState<List<Restaurant>>>(UiState.Idle)
        private set

    var restaurantMenu by mutableStateOf<UiState<Map<String, List<FoodItem>>>>(UiState.Idle)
        private set

    var selectedRestaurant by mutableStateOf<Restaurant?>(null)
        private set

    var foodItemDetail by mutableStateOf<UiState<FoodItem>>(UiState.Idle)
        private set

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            repo.categories().onSuccess { categories = it }
        }
        loadRestaurants()
    }

    fun loadRestaurants(search: String? = null) {
        restaurants = UiState.Loading
        viewModelScope.launch {
            repo.restaurants(search = search)
                .onSuccess { restaurants = UiState.Success(it.data) }
                .onFailure { restaurants = UiState.Error(it.message ?: "Could not load restaurants") }
        }
    }

    fun openRestaurant(id: Int) {
        restaurantMenu = UiState.Loading
        viewModelScope.launch {
            repo.restaurantDetail(id).onSuccess { selectedRestaurant = it }
            repo.restaurantMenu(id)
                .onSuccess { restaurantMenu = UiState.Success(it) }
                .onFailure { restaurantMenu = UiState.Error(it.message ?: "Could not load menu") }
        }
    }

    fun openFoodItem(id: Int) {
        foodItemDetail = UiState.Loading
        viewModelScope.launch {
            repo.foodItemDetail(id)
                .onSuccess { foodItemDetail = UiState.Success(it) }
                .onFailure { foodItemDetail = UiState.Error(it.message ?: "Could not load item") }
        }
    }
}
