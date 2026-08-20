package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeevifood.customer.viewmodel.CartViewModel
import com.jeevifood.customer.viewmodel.CatalogViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun FoodItemDetailScreen(
    foodId: Int,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onAddedToCart: () -> Unit
) {
    LaunchedEffect(foodId) { catalogViewModel.openFoodItem(foodId) }

    var quantity by remember { mutableStateOf(1) }
    // variationId -> selected optionIds
    val selectedOptions = remember { mutableStateMapOf<Int, MutableSet<Int>>() }
    val selectedAddons = remember { mutableStateListOf<Int>() }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Food Details") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
        )

        when (val state = catalogViewModel.foodItemDetail) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UiState.Success -> {
                val item = state.data
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
                    AsyncImage(model = item.image, contentDescription = item.name, modifier = Modifier.fillMaxWidth().height(200.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (!item.description.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(item.description, fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "₹${item.discountPrice ?: item.price}",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )

                    item.variations.forEach { variation ->
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "${variation.name}${if (variation.isRequired) " *" else ""}",
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                        )
                        variation.options.forEach { option ->
                            val currentSet = selectedOptions.getOrPut(variation.id) { mutableStateSetOf() }
                            val isSelected = option.id in currentSet
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().selectable(
                                    selected = isSelected,
                                    onClick = {
                                        if (variation.maxSelectable <= 1) {
                                            currentSet.clear()
                                            currentSet.add(option.id)
                                        } else {
                                            if (isSelected) currentSet.remove(option.id) else currentSet.add(option.id)
                                        }
                                    }
                                ).padding(vertical = 4.dp)
                            ) {
                                if (variation.maxSelectable <= 1) {
                                    RadioButton(selected = isSelected, onClick = null)
                                } else {
                                    Checkbox(checked = isSelected, onCheckedChange = null)
                                }
                                Text(option.label, modifier = Modifier.weight(1f))
                                if (option.additionalPrice > 0) Text("+₹${option.additionalPrice}", fontSize = 12.sp)
                            }
                        }
                    }

                    if (item.addons.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Text("Add-ons", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        item.addons.forEach { addon ->
                            val isSelected = addon.id in selectedAddons
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().selectable(
                                    selected = isSelected,
                                    onClick = { if (isSelected) selectedAddons.remove(addon.id) else selectedAddons.add(addon.id) }
                                ).padding(vertical = 4.dp)
                            ) {
                                Checkbox(checked = isSelected, onCheckedChange = null)
                                Text(addon.name, modifier = Modifier.weight(1f))
                                Text("+₹${addon.price}", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Quantity", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { if (quantity > 1) quantity-- }) { Text("−", fontSize = 20.sp) }
                        Text("$quantity", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { quantity++ }) { Text("+", fontSize = 20.sp) }
                    }
                }

                Button(
                    onClick = {
                        cartViewModel.addItem(
                            foodItemId = item.id,
                            quantity = quantity,
                            variationOptionIds = selectedOptions.values.flatten(),
                            addonIds = selectedAddons.toList()
                        )
                        onAddedToCart()
                    },
                    enabled = item.isAvailable,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
                ) {
                    Text(if (item.isAvailable) "Cart-க்கு சேர்" else "இப்போ கிடைக்கல")
                }
            }
        }
    }
}
