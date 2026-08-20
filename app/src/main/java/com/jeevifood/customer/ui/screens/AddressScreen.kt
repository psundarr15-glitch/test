package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.data.Address
import com.jeevifood.customer.viewmodel.AddressViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun AddressScreen(
    viewModel: AddressViewModel,
    selectable: Boolean = false,
    selectedAddressId: Int? = null,
    onSelect: ((Address) -> Unit)? = null,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.loadAddresses() }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Addresses") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
            actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Filled.Add, contentDescription = "Add") } }
        )

        when (val state = viewModel.addresses) {
            is UiState.Loading, UiState.Idle -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("முகவரி எதுவும் இல்ல. + பட்டன் அழுத்தி சேருங்க") }
                } else {
                    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.data) { addr ->
                            Card(
                                modifier = Modifier.fillMaxWidth().let {
                                    if (selectable && onSelect != null) it.selectable(selected = addr.id == selectedAddressId, onClick = { onSelect(addr) }) else it
                                }
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (selectable) {
                                        RadioButton(selected = addr.id == selectedAddressId, onClick = { onSelect?.invoke(addr) })
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(addr.address_type.uppercase(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(addr.address_line, fontSize = 13.sp)
                                        if (!addr.landmark.isNullOrBlank()) Text(addr.landmark, fontSize = 12.sp)
                                    }
                                    if (!selectable) {
                                        TextButton(onClick = { addr.id?.let { viewModel.deleteAddress(it) } }) { Text("Delete") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAddressDialog(
            onDismiss = { showAddDialog = false },
            onSave = { addr -> viewModel.addAddress(addr) { showAddDialog = false } }
        )
    }
}

@Composable
private fun AddAddressDialog(onDismiss: () -> Unit, onSave: (Address) -> Unit) {
    var line by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("புது முகவரி சேர்") },
        text = {
            Column {
                OutlinedTextField(line, { line = it }, label = { Text("Address Line") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(landmark, { landmark = it }, label = { Text("Landmark") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(contactName, { contactName = it }, label = { Text("Contact Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(contactPhone, { contactPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                Text(
                    "குறிப்பு: latitude/longitude துல்லியமா வேணும்னா, GPS location picker-ஐ பின்னாடி integrate பண்ணலாம். இப்போதைக்கு 0.0 default-ஆ அனுப்பப்படும்.",
                    fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    Address(
                        address_line = line,
                        landmark = landmark.ifBlank { null },
                        city = city.ifBlank { null },
                        contact_person_name = contactName.ifBlank { null },
                        contact_person_number = contactPhone.ifBlank { null },
                        latitude = 0.0,
                        longitude = 0.0
                    )
                )
            }, enabled = line.isNotBlank()) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

