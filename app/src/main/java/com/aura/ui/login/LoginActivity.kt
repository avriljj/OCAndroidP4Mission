package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.ui.viewModel.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
class LoginActivity : ComponentActivity() {

  private lateinit var binding: ActivityLoginBinding

  private lateinit var userViewModel: UserViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Initialize ViewModel using ViewModelProvider
    userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

    val login = binding.login
    val loading = binding.loading

    // Observe form validity
    lifecycleScope.launch {
      userViewModel.isFormValid.collect { isValid ->
        login.isEnabled = isValid
      }
    }

    binding.identifier.addTextChangedListener { text ->
      userViewModel.onUsernameChanged(text.toString())
    }

    binding.password.addTextChangedListener { text ->
      userViewModel.onPasswordChanged(text.toString())
    }

    login.setOnClickListener {
      loading.visibility = View.VISIBLE

      val intent = Intent(this@LoginActivity, HomeActivity::class.java)
      startActivity(intent)

      finish()
    }
  }
}
