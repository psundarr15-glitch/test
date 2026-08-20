package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.data.CartItem
import com.jeevifood.customer.viewmodel.CartViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadCart() }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Cart") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
        )

        when (val state = viewModel.cart) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UiState.Success -> {
                val cart = state.data
                if (cart.items.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("உங்க Cart காலியா இருக்கு") }
                } else {
                    LazyColumn(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(cart.items) { ci ->
                            CartItemRow(
                                item = ci,
                                onIncrease = { viewModel.updateQuantity(ci.id, ci.quantity + 1) },
                                onDecrease = { viewModel.updateQuantity(ci.id, ci.quantity - 1) },
                                onRemove = { viewModel.removeItem(ci.id) }
                            )
                        }
                    }

                    Divider()
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Item Total", modifier = Modifier.weight(1f))
                            Text("₹${cart.itemTotal}")
                        }
                        viewModel.appliedCoupon?.let { coupon ->
                            Spacer(Modifier.height(4.dp))
                            Row(Modifier.fillMaxWidth()) {
                                Text("Coupon (${coupon.code})", modifier = Modifier.weight(1f))
                                Text("-₹${coupon.discountAmount}")
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Text("Checkout செய்ய")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.foodItem?.name ?: "Item #${item.foodItemId}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("₹${item.itemPrice} x ${item.quantity}", fontSize = 12.sp)
            }
            IconButton(onClick = onDecrease) { Text("−", fontSize = 18.sp) }
            Text("${item.quantity}")
            IconButton(onClick = onIncrease) { Text("+", fontSize = 18.sp) }
            IconButton(onClick = onRemove) { Icon(Icons.Filled.Delete, contentDescription = "Remove") }
        }
    }
}
