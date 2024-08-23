package com.aura.ui.data.account

data class User(
    val id: String,
    val firstname: String,
    val lastname: String,
    val password: String,
    val accounts: List<Account>,
)
