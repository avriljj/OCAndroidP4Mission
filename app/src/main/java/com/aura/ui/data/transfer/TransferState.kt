package com.aura.ui.data.transfer

sealed class TransferState {
    object Idle : TransferState() // État initial ou inactif
    object Loading : TransferState() // Lorsque le transfert est en cours
    data class Success(val message: String) : TransferState() // Lorsque le transfert est réussi
    data class Error(val errorMessage: String) : TransferState() // En cas d'échec du transfert
}
