package com.aura.ui.transfer

import android.os.Bundle
import android.util.Log
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

        // Observe loading state to manage button and progress bar visibility
        lifecycleScope.launch {
            transferViewModel.isLoading.collectLatest { isLoading ->
              //  binding.transfer.isEnabled = !isLoading
                binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe the transfer result to handle success or failure
        lifecycleScope.launch {
            transferViewModel.transferResult.collectLatest { isSuccessful ->
                if (isSuccessful == true) {
                    Toast.makeText(this@TransferActivity, "Transfer successful", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else if (isSuccessful == false) {
                    binding.transfer.isEnabled = false
                    binding.loading.visibility = View.GONE
                }
            }
        }

        // Observe error messages and display them when available
        lifecycleScope.launch {
            transferViewModel.errorMessage.collectLatest { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(this@TransferActivity, it, Toast.LENGTH_LONG).show()
                    binding.transfer.isEnabled = false
                }
            }
        }
    }
}

