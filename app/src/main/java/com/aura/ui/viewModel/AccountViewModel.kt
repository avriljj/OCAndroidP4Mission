import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.ui.data.account.Account
import com.aura.ui.data.account.AccountState
import com.aura.ui.data.network.ApiClient
import com.aura.ui.data.network.UserApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AccountViewModel : ViewModel() {

    private val userApiService: UserApiService = ApiClient.apiService

    // LiveData to hold loading state
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _accountState = MutableStateFlow<AccountState>(AccountState.Idle)
    val accountState: StateFlow<AccountState> = _accountState

    fun getAccountsByUserId(userId: String) {
        viewModelScope.launch {
            _accountState.value = AccountState.Loading // Début du chargement
            try {
                val response: Response<List<Account>> = userApiService.getAccounts(userId)
                if (response.isSuccessful) {
                    val accounts = response.body() ?: emptyList()
                    _accountState.value = AccountState.Success(accounts)
                } else {
                    _accountState.value = AccountState.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _accountState.value = AccountState.Error("Exception: ${e.message}")
            }
        }
    }


}
