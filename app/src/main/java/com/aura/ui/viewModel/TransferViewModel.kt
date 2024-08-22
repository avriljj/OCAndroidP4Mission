package com.aura.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.ApiClient
import com.aura.ui.data.network.UserApiService
import com.aura.ui.data.transfer.Transfer
import com.aura.ui.data.transfer.TransferResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class TransferViewModel : ViewModel() {

    private val userApiService: UserApiService = ApiClient.apiService

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _transferResult = MutableStateFlow<Boolean?>(null)
    val transferResult: StateFlow<Boolean?> = _transferResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    private val recipient = MutableStateFlow("")
    private val amount = MutableStateFlow("")

    init {
        // Initial validation to ensure button is disabled on start
        validateForm()
    }

    fun onRecipientChanged(newRecipient: String) {
        recipient.value = newRecipient
        validateForm()
    }

    fun onAmountChanged(newAmount: String) {
        amount.value = newAmount
        validateForm()
    }

    private fun validateForm() {
        _isFormValid.value = recipient.value.isNotBlank() && amount.value.isNotBlank()
    }
    // Perform the transfer operation
    fun performTransfer(senderId: String, recipient: String, amount: String) {

        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            _errorMessage.value = "Invalid amount: $amount"
            _transferResult.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true  // Start loading
            _errorMessage.value = null // Reset error message
            try {
                val response: Response<TransferResult> = userApiService.transfer(
                    Transfer(
                        sender = senderId,
                        recipient = recipient,
                        amount = amountDouble
                    )
                )

                if (response.isSuccessful) {
                    _transferResult.value = true
                } else {
                    _transferResult.value = false
                    _errorMessage.value = "Transfer failed with code: ${response.code()}"
                }
            } catch (e: Exception) {
                _transferResult.value = false
                _errorMessage.value = "Transfer failed: ${e.message}"
            } finally {
                _isLoading.value = false  // Stop loading
            }
        }
    }
}


