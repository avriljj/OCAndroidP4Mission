package com.aura.ui.viewModel

import LoginRequest
import LoginResponse
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid

    private val _loginResult = MutableStateFlow<LoginResponse?>(null)
    val loginResult: StateFlow<LoginResponse?> = _loginResult.asStateFlow()

    private val _username = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    fun onUsernameChanged(username: String) {
        _username.value = username
        validateForm()
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
        validateForm()
    }

    private fun validateForm() {
        _isFormValid.value = _username.value.isNotBlank() && _password.value.isNotBlank()
    }

    fun login() {
        if (_isFormValid.value) {
            viewModelScope.launch {
                try {
                    val response: Response<LoginResponse> = ApiClient.apiService.login(
                        LoginRequest(_username.value, _password.value)
                    )

                    if (response.isSuccessful) {
                        _loginResult.value = response.body()
                        Log.d("UserViewModel", "Login successful: ${response.body()}")
                    } else {
                        _loginResult.value = LoginResponse(granted = false)  // Explicit failure case
                        Log.e("UserViewModel", "Login failed with response code: ${response.code()} and message: ${response.message()}")
                    }
                } catch (e: Exception) {
                    _loginResult.value = LoginResponse(granted = false)  // Handle exception case
                    Log.e("UserViewModel", "Login failed with exception: ", e)
                }
            }
        }
    }

    // Call this to reset the loginResult after handling it in the Activity
    fun resetLoginResult() {
        _loginResult.value = null
    }
}
