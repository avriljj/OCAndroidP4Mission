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
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.aura.ui.data.transfer.TransferState
import com.aura.ui.viewModel.UserViewModel
import com.google.android.material.snackbar.Snackbar


class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private lateinit var transferViewModel: TransferViewModel

    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transfer.isEnabled = false

        transferViewModel= ViewModelProvider(this).get(TransferViewModel::class.java)

        lifecycleScope.launch {
            transferViewModel.isFormValid.collect { isValid ->
                binding.transfer.isEnabled = isValid
            }
        }

        // Inside Activity
        if (!this.isNetworkAvailable()) {  // 'this' refers to the Activity's Context
            showSnackbar(binding.root, "No network connection available")
            Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show()

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

            // Inside Activity
            if (!this.isNetworkAvailable()) {  // 'this' refers to the Activity's Context
                showSnackbar(binding.root, "No network connection available")
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show()

            }

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
                        //binding.transfer.isEnabled = false
                        binding.transfer.isEnabled = transferViewModel.isFormValid.value // Check form validity
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

