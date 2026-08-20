package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.viewmodel.*

@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    addressViewModel: AddressViewModel,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit,
    onManageAddresses: () -> Unit,
    onOrderPlaced: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        cartViewModel.loadCart()
        addressViewModel.loadAddresses()
    }

    var selectedAddressId by remember { mutableStateOf<Int?>(null) }
    var paymentMethod by remember { mutableStateOf("cod") }
    var couponCode by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    val checkoutState = orderViewModel.checkoutState
    LaunchedEffect(checkoutState) {
        if (checkoutState is UiState.Success) {
            onOrderPlaced(checkoutState.data.id)
            orderViewModel.resetCheckout()
        }
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Checkout") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
        )

        Column(Modifier.weight(1f).padding(16.dp)) {
            Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(6.dp))

            val addressState = addressViewModel.addresses
            if (addressState is UiState.Success) {
                if (addressState.data.isEmpty()) {
                    Text("முகவரி இல்ல", fontSize = 13.sp)
                } else {
                    addressState.data.forEach { addr ->
                        if (selectedAddressId == null && addr.is_default) selectedAddressId = addr.id
                        Row(
                            Modifier.fillMaxWidth().selectable(
                                selected = addr.id == selectedAddressId,
                                onClick = { selectedAddressId = addr.id }
                            ).padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = addr.id == selectedAddressId, onClick = { selectedAddressId = addr.id })
                            Text(addr.address_line, fontSize = 13.sp)
                        }
                    }
                }
            }
            TextButton(onClick = onManageAddresses) { Text("முகவரிகளை நிர்வகி") }

            Spacer(Modifier.height(16.dp))
            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            listOf("cod" to "Cash on Delivery", "online" to "Online Payment", "wallet" to "Wallet").forEach { (value, label) ->
                Row(
                    Modifier.fillMaxWidth().selectable(selected = paymentMethod == value, onClick = { paymentMethod = value }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = paymentMethod == value, onClick = { paymentMethod = value })
                    Text(label, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Coupon", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(couponCode, { couponCode = it }, label = { Text("Coupon Code") }, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Button(onClick = { cartViewModel.applyCoupon(couponCode.trim()) }, enabled = couponCode.isNotBlank()) { Text("Apply") }
            }
            cartViewModel.appliedCoupon?.let { c -> Text("Discount: ₹${c.discountAmount} applied", fontSize = 12.sp) }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                instructions, { instructions = it },
                label = { Text("Delivery Instructions (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (checkoutState is UiState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(checkoutState.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }
        }

        Button(
            onClick = {
                selectedAddressId?.let { addrId ->
                    orderViewModel.placeOrder(addrId, paymentMethod, couponCode.trim().ifBlank { null }, instructions.trim().ifBlank { null })
                }
            },
            enabled = selectedAddressId != null && checkoutState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp)
        ) {
            if (checkoutState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Order-ஐ உறுதிப்படுத்து")
            }
        }
    }
}
