package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.viewModel.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

  private lateinit var binding: ActivityLoginBinding
  private lateinit var userViewModel: UserViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

    lifecycleScope.launch {
      userViewModel.isFormValid.collect { isValid ->
        binding.login.isEnabled = isValid
      }
    }

    binding.identifier.addTextChangedListener { text ->
      userViewModel.onUsernameChanged(text.toString())
    }

    binding.password.addTextChangedListener { text ->
      userViewModel.onPasswordChanged(text.toString())
    }

    binding.login.setOnClickListener {
      binding.loading.visibility = View.VISIBLE
      userViewModel.login()
      binding.login.isEnabled = false
    }

    // Observe the login result
    lifecycleScope.launch {
      userViewModel.loginResult.collect { loginResponse ->


        if (loginResponse?.granted == true) {
          binding.loading.visibility = View.GONE
          Log.d("LoginActivity", "Login successful, navigating to HomeActivity")
          val intent = Intent(this@LoginActivity, HomeActivity::class.java)
          intent.putExtra("USER_ID", binding.identifier.text.toString())
          startActivity(intent)
          finish()
        } else if (loginResponse?.granted == false) {
          binding.loading.visibility = View.GONE
          binding.login.isEnabled = true
          Toast.makeText(this@LoginActivity, "Login failed: Invalid credentials", Toast.LENGTH_LONG).show()
        }

        // Reset loginResult to avoid double handling
        userViewModel.resetLoginResult()
      }
    }
  }
}
