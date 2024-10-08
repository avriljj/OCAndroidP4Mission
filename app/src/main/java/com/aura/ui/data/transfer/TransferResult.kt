package com.aura.ui.data.transfer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents the result of a transfer.
 * @property result Whether the transfer was successful.
 */
@Serializable
data class TransferResult(
    @SerialName("result") val result: Boolean,
)
