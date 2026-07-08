package com.bussiness.curemegptapp.viewmodel.loginviewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginData
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.ui.uistate.LoginUiState
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.util.ValidationUtils
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    private var fcmToken by mutableStateOf<String?>(null)

    init {
        fetchToken()
    }
    fun onEmailPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(emailOrPhone = value)
    }
    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }
    private fun fetchToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result
                    Log.d("FCM", "FCM Token: ${task.result}")
                } else {
                    fcmToken = "Fetching FCM token failed"
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }

    fun loginRequest(onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val emailOrPhoneValidation = ValidationUtils.validateEmailOrPhone(state.emailOrPhone)
        val passwordValidation = ValidationUtils.validatePassword(state.password)

        if (!emailOrPhoneValidation.isValid) {
            onError(emailOrPhoneValidation.errorMessage)
            return
        }

        if (!passwordValidation.isValid) {
            onError(passwordValidation.errorMessage)
            return
        }

        val loginValidation = ValidationUtils.validateLoginCredentials(state.emailOrPhone, state.password)

        if (!loginValidation.isValid) {
            onError(loginValidation.errorMessage)
            return
        }
        viewModelScope.launch {
            repository.loginRequest(state.emailOrPhone, state.password,fcmToken.toString())
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val data = result.data.data
                            data?.let { userData->
                                sessionManager.setToken(userData.token?:"")
                                sessionManager.setUserId(userData.user?.id.toString())
                                sessionManager.setUserName(userData.user?.name.toString())
                                sessionManager.setLogin(true)
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



