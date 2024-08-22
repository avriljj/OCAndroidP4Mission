package com.aura.ui.data.transfer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transfer(
    @SerialName("sender") val sender: String,
    @SerialName("recipient") val recipient: String,
    @SerialName("amount") val amount: Double
)