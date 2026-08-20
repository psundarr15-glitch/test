package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeevifood.customer.data.Category
import com.jeevifood.customer.data.Restaurant
import com.jeevifood.customer.viewmodel.CatalogViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun HomeScreen(
    viewModel: CatalogViewModel,
    onRestaurantClick: (Int) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("என்ன சாப்பிடலாம் இன்னிக்கு?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Restaurant அல்லது food தேடுங்க") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onSearch = { viewModel.loadRestaurants(query.ifBlank { null }) }
            )
        )

        if (viewModel.categories.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.categories) { cat -> CategoryChip(cat) }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("உங்க அருகில் உள்ள Restaurants", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))

        when (val state = viewModel.restaurants) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message)
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Restaurants கிடைக்கல") }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.data) { r -> RestaurantCard(r) { onRestaurantClick(r.id) } }
                        item { Spacer(Modifier.height(60.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(64.dp)) {
        AsyncImage(
            model = category.image,
            contentDescription = category.name,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.height(4.dp))
        Text(category.name, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
private fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column {
            AsyncImage(
                model = restaurant.coverPhoto ?: restaurant.logo,
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxWidth().height(140.dp),
            )
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(" ${restaurant.avgRating}", fontSize = 13.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(restaurant.address ?: "", fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(restaurant.deliveryTime ?: "", fontSize = 12.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("Min ₹${restaurant.minimumOrder.toInt()}", fontSize = 12.sp)
                    if (!restaurant.isOpenNow) {
                        Spacer(Modifier.width(12.dp))
                        Text("மூடியிருக்கு", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
