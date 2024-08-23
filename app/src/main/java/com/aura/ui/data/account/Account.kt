package com.aura.ui.data.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String,
    @SerialName("main") val main: Boolean,
    @SerialName("balance") var balance: Double
)