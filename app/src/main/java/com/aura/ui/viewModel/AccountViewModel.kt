import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.Account
import com.aura.ui.data.network.ApiClient
import com.aura.ui.data.network.UserApiService
import kotlinx.coroutines.launch
import retrofit2.Response

class AccountViewModel : ViewModel() {

    private val userApiService: UserApiService = ApiClient.apiService

    // LiveData to hold a list of account information
    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    // LiveData to hold loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // LiveData to hold error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to fetch account information by user ID
    fun getAccountsByUserId(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response: Response<List<Account>> = userApiService.getAccounts(userId)
                if (response.isSuccessful) {
                    _accounts.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
