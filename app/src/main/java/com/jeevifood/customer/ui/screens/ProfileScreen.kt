package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onAddresses: () -> Unit,
    onOrders: () -> Unit,
    onLoggedOut: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        ListItem(headlineContent = { Text("என் Addresses") }, modifier = Modifier.clickableRow(onAddresses))
        Divider()
        ListItem(headlineContent = { Text("என் Orders") }, modifier = Modifier.clickableRow(onOrders))
        Divider()

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = { viewModel.logout(onLoggedOut) }, modifier = Modifier.fillMaxWidth()) {
            Text("Logout")
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))
