package com.aura.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.ApiClient
import com.aura.ui.data.network.UserApiService
import com.aura.ui.data.transfer.Transfer
import com.aura.ui.data.transfer.TransferResult
import com.aura.ui.data.transfer.TransferState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class TransferViewModel : ViewModel() {

    private val userApiService: UserApiService = ApiClient.apiService

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid

    private val recipient = MutableStateFlow("")
    private val amount = MutableStateFlow("")

    private val _transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    val transferState: StateFlow<TransferState> = _transferState

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

    fun performTransfer(senderId: String, recipient: String, amount: String) {
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            _transferState.value = TransferState.Error("Invalid amount: $amount")
            return
        }

        viewModelScope.launch {
            _transferState.value = TransferState.Loading
            try {
                val response: Response<TransferResult> = userApiService.transfer(
                    Transfer(sender = senderId, recipient = recipient, amount = amountDouble)
                )
                if (response.isSuccessful) {
                    _transferState.value = TransferState.Success("Transfer successful")
                } else {
                    _transferState.value = TransferState.Error("Transfer failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                _transferState.value = TransferState.Error("Transfer failed: ${e.message}")
            } finally {
                _transferState.value = TransferState.Idle
            }
        }
    }
}
