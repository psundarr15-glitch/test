package com.jeevifood.customer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeevifood.customer.viewmodel.AuthViewModel
import com.jeevifood.customer.viewmodel.UiState

@Composable
fun OtpScreen(
    phone: String,
    viewModel: AuthViewModel,
    onVerified: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    val state = viewModel.otpState

    LaunchedEffect(Unit) { viewModel.sendOtp(phone) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("OTP சரிபார்ப்பு", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("$phone -க்கு அனுப்பப்பட்ட OTP-ஐ உள்ளிடவும்", fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )

        if (state is UiState.Error) {
            Spacer(Modifier.height(8.dp))
            Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { viewModel.verifyOtp(phone, otp, onVerified) },
            enabled = otp.length in 4..6 && state !is UiState.Loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (state is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("சரிபார்")
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { viewModel.sendOtp(phone) }) {
            Text("OTP-ஐ மறுபடி அனுப்பு")
        }
    }
}
