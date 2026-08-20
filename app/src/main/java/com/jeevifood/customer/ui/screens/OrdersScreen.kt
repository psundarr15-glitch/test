package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.data.Order
import com.jeevifood.customer.viewmodel.OrderViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun OrdersScreen(viewModel: OrderViewModel, onOrderClick: (Int) -> Unit) {
    LaunchedEffect(Unit) { viewModel.loadOrders() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("என் Orders", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        when (val state = viewModel.orders) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("இதுவரை Order எதுவும் இல்ல") }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.data) { order -> OrderRow(order) { onOrderClick(order.id) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderRow(order: Order, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row {
                Text("#${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(order.orderStatus.replace("_", " "), fontSize = 12.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(order.restaurant?.name ?: "", fontSize = 13.sp)
            Text("₹${order.totalAmount}", fontSize = 13.sp)
        }
    }
}

@Composable
fun OrderDetailScreen(orderId: Int, viewModel: OrderViewModel, onBack: () -> Unit) {
    LaunchedEffect(orderId) { viewModel.loadOrderDetail(orderId) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Order Details") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
        )

        when (val state = viewModel.orderDetail) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UiState.Success -> {
                val order = state.data
                Column(Modifier.padding(16.dp)) {
                    Text("Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Status: ${order.orderStatus.replace("_", " ")}", fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(order.restaurant?.name ?: "", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))

                    order.details?.forEach { d ->
                        Row(Modifier.fillMaxWidth()) {
                            Text("${d.itemName} x${d.quantity}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text("₹${d.subtotal}", fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("Item Total", modifier = Modifier.weight(1f))
                        Text("₹${order.itemTotal}")
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text("Delivery Fee", modifier = Modifier.weight(1f))
                        Text("₹${order.deliveryFee}")
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text("Discount", modifier = Modifier.weight(1f))
                        Text("-₹${order.discountAmount}")
                    }
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text("₹${order.totalAmount}", fontWeight = FontWeight.Bold)
                    }

                    if (order.orderStatus in listOf("pending", "confirmed")) {
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = { showCancelDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Order-ஐ ரத்து செய்")
                        }
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        var reason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Order-ஐ ரத்து செய்யவா?") },
            text = {
                OutlinedTextField(reason, { reason = it }, label = { Text("காரணம் (optional)") })
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelOrder(orderId, reason) { showCancelDialog = false }
                }) { Text("ரத்து செய்") }
            },
            dismissButton = { TextButton(onClick = { showCancelDialog = false }) { Text("Close") } }
        )
    }
}
