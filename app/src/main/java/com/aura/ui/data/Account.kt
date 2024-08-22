package com.aura.ui.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id") val id: String,
    @SerialName("main") val main: Boolean,
    @SerialName("balance") var balance: Double
)