package com.jeevifood.customer.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeevifood.customer.data.Address
import com.jeevifood.customer.repository.AddressRepository
import kotlinx.coroutines.launch

class AddressViewModel(private val repo: AddressRepository) : ViewModel() {

    var addresses by mutableStateOf<UiState<List<Address>>>(UiState.Idle)
        private set

    var saveState by mutableStateOf<UiState<Unit>>(UiState.Idle)
        private set

    fun loadAddresses() {
        addresses = UiState.Loading
        viewModelScope.launch {
            repo.list()
                .onSuccess { addresses = UiState.Success(it) }
                .onFailure { addresses = UiState.Error(it.message ?: "Could not load addresses") }
        }
    }

    fun addAddress(address: Address, onDone: () -> Unit) {
        saveState = UiState.Loading
        viewModelScope.launch {
            repo.add(address)
                .onSuccess {
                    saveState = UiState.Success(Unit)
                    loadAddresses()
                    onDone()
                }
                .onFailure { saveState = UiState.Error(it.message ?: "Could not save address") }
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            repo.delete(id).onSuccess { loadAddresses() }
        }
    }
}
