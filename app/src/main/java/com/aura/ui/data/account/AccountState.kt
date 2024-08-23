package com.aura.ui.data.account

sealed class AccountState {
    object Loading : AccountState()
    data class Success(val accounts: List<Account>) : AccountState()
    data class Error(val message: String) : AccountState()
    object Idle : AccountState() // État initial, avant que toute action ne soit entreprise
}
