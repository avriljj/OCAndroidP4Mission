package com.aura.ui.home

import AccountViewModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity


/**
 * The home activity for the app.
 */
class HomeActivity : AppCompatActivity() {

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  private lateinit var accountViewModel: AccountViewModel

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      if (result.resultCode == Activity.RESULT_OK) {
        // Refresh the balance because the transfer was successful
        val userId = intent.getStringExtra("USER_ID") // Retrieve the userId again
        if (!userId.isNullOrEmpty()) {
          loadAccountsData(userId)
        } else {
          Toast.makeText(this, "User ID is missing. Cannot refresh balance.", Toast.LENGTH_LONG).show()
        }
      }
    }


  // Method that starts TransferActivity
  private fun startTransfer(userId: String) {
    val intent = Intent(this@HomeActivity, TransferActivity::class.java)
    intent.putExtra("USER_ID", userId) // Pass the user ID
    startTransferActivityForResult.launch(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    accountViewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val userId = intent.getStringExtra("USER_ID")

    // Ensure userId is not null or empty
    if (!userId.isNullOrEmpty()) {
      // Load the accounts data initially
      loadAccountsData(userId)

      // Set a click listener on the reload button to reload data
      binding.loginMainScreen.setOnClickListener {
        loadAccountsData(userId) // Reload the data when the button is clicked
      }

      // Set a click listener on the transfer button to start TransferActivity
      binding.transfer.setOnClickListener {
        startTransfer(userId)
      }

    } else {
      // Handle the case where userId is null or empty
      Toast.makeText(this, "User ID is missing", Toast.LENGTH_LONG).show()
    }
  }

  /**
   * Function to load accounts data.
   */
  private fun loadAccountsData(userId: String) {
    binding.loadingMainScreen.visibility = View.VISIBLE

    // Trigger the API call to fetch accounts
    accountViewModel.getAccountsByUserId(userId)

    // Observe the accounts LiveData
    accountViewModel.accounts.observe(this, Observer { accounts ->
      if (!accounts.isNullOrEmpty()) {
        // Find the account where 'main' is true
        val mainAccount = accounts.find { it.main }
        if (mainAccount != null) {
          // Display the balance of the main account
          binding.balance.text = "${mainAccount.balance}€"
          binding.loginMainScreen.visibility = View.GONE // Hide the reload button
          binding.loadingMainScreen.visibility = View.GONE
        } else {
          Toast.makeText(this, "No main account found", Toast.LENGTH_LONG).show()
          binding.loginMainScreen.visibility = View.VISIBLE // Show the reload button
          binding.loadingMainScreen.visibility = View.VISIBLE
        }
      } else {
        Toast.makeText(this, "No accounts found", Toast.LENGTH_LONG).show()
        binding.loadingMainScreen.visibility = View.VISIBLE
      }
    })
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.disconnect -> {
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}
