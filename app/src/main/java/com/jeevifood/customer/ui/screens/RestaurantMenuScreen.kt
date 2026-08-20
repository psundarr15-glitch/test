package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeevifood.customer.data.FoodItem
import com.jeevifood.customer.viewmodel.CatalogViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun RestaurantMenuScreen(
    restaurantId: Int,
    viewModel: CatalogViewModel,
    onBack: () -> Unit,
    onFoodItemClick: (Int) -> Unit,
    onCartClick: () -> Unit
) {
    LaunchedEffect(restaurantId) { viewModel.openRestaurant(restaurantId) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(viewModel.selectedRestaurant?.name ?: "Menu") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
            },
            actions = {
                IconButton(onClick = onCartClick) { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") }
            }
        )

        when (val state = viewModel.restaurantMenu) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message)
            }
            is UiState.Success -> {
                val grouped = state.data
                if (grouped.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("இந்த restaurant-ல items இல்ல") }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        grouped.forEach { (categoryName, items) ->
                            item {
                                Text(categoryName, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                            items(items) { food -> FoodItemRow(food) { onFoodItemClick(food.id) } }
                        }
                        item { Spacer(Modifier.height(60.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodItemRow(item: FoodItem, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(14.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (item.isVeg) Color(0xFF2E7D32) else Color(0xFFC62828))
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                if (!item.description.isNullOrBlank()) {
                    Text(item.description, fontSize = 12.sp, maxLines = 2)
                }
                Spacer(Modifier.height(4.dp))
                if (item.discountPrice != null) {
                    Row {
                        Text("₹${item.discountPrice}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("₹${item.price}", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    Text("₹${item.price}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                if (!item.isAvailable) {
                    Text("இப்போ கிடைக்கல", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.width(8.dp))
            AsyncImage(
                model = item.image,
                contentDescription = item.name,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
