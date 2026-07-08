package com.bussiness.curemegptapp.viewmodel.forgotviewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginData
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.ui.uistate.SignUpUiState
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()
    fun onEmailPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(emailOrPhone = value)
    }
    fun forgotOtpRequest(onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val emailOrPhoneValidation = ValidationUtils.validateEmailOrPhone(state.emailOrPhone)
        if (!emailOrPhoneValidation.isValid) {
            onError(emailOrPhoneValidation.errorMessage)
            return
        }
        viewModelScope.launch {
            repository.forgotOtpRequest( state.emailOrPhone)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val data = result.data.data
                            data?.let { userData->
                                onSuccess(userData)
                            }
                        }
                        is Resource.Error -> {
                            LoaderManager.hide()
                            onError(result.message)
                        }
                        Resource.Idle -> Unit
                    }
                }
        }
    }
}



