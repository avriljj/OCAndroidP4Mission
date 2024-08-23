package com.aura.ui.transfer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.viewModel.TransferViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import android.app.Activity
import android.view.View
import com.aura.ui.data.transfer.TransferState


class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private val transferViewModel: TransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Disable the button initially
        binding.transfer.isEnabled = false

        // Observe form validation state to enable/disable the transfer button
        lifecycleScope.launch {
            transferViewModel.isFormValid.collect { isValid ->
                binding.transfer.isEnabled = isValid
            }
        }

        binding.recipient.addTextChangedListener { text ->
            transferViewModel.onRecipientChanged(text.toString())
        }

        binding.amount.addTextChangedListener { text ->
            transferViewModel.onAmountChanged(text.toString())
        }

        val userId = intent.getStringExtra("USER_ID")

        binding.transfer.setOnClickListener {
            val recipientText = binding.recipient.text.toString()
            val amountText = binding.amount.text.toString()

            if (recipientText.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Recipient and amount must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            transferViewModel.performTransfer(userId!!, recipientText, amountText)
        }

        lifecycleScope.launch {
            transferViewModel.transferState.collectLatest { state ->
                when (state) {
                    is TransferState.Idle -> {
                        binding.transfer.isEnabled = true
                        binding.loading.visibility = View.GONE
                    }
                    is TransferState.Loading -> {
                        binding.transfer.isEnabled = false
                        binding.loading.visibility = View.VISIBLE
                    }
                    is TransferState.Success -> {
                        binding.transfer.isEnabled = true
                        binding.loading.visibility = View.GONE
                        Toast.makeText(this@TransferActivity, state.message, Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    is TransferState.Error -> {
                        binding.transfer.isEnabled = true
                        binding.loading.visibility = View.GONE
                        Toast.makeText(this@TransferActivity, state.errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

