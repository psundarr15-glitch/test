package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.viewmodel.AuthViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegistered: (phone: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var fName by remember { mutableStateOf("") }
    var lName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val state = viewModel.registerState
    LaunchedEffect(state) {
        if (state is UiState.Success) onRegistered(phone.trim())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("கணக்கு உருவாக்குங்க", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(fName, { fName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(lName, { lName = it }, label = { Text("Last Name (optional)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            phone, { phone = it }, label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            email, { email = it }, label = { Text("Email (optional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            password, { password = it }, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            confirm, { confirm = it }, label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (state is UiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.register(fName.trim(), lName.trim().ifBlank { null }, phone.trim(), email.trim().ifBlank { null }, password, confirm) },
            enabled = fName.isNotBlank() && phone.isNotBlank() && password.isNotBlank() && password == confirm && state !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Register")
            }
        }
        if (password.isNotBlank() && confirm.isNotBlank() && password != confirm) {
            Text("Passwords match ஆகல", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBackToLogin) { Text("ஏற்கனவே கணக்கு இருக்கா? Login") }
    }
}
