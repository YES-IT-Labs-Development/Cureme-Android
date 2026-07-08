package com.bussiness.curemegptapp.viewmodel.signupviewmodel
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
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.messaging.FirebaseMessaging

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()
    private var fcmToken by mutableStateOf<String?>(null)

    init {
        fetchToken()
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

    fun onEmailPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(emailOrPhone = value)
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onCnfPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(cnfPassword  = value)
    }

    fun registerRequest(onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val nameValidation = ValidationUtils.validateName(state.name)
        val emailOrPhoneValidation = ValidationUtils.validateEmailOrPhone(state.emailOrPhone)
        val passwordValidation = ValidationUtils.validatePassword(state.password)
        val confirmPasswordValidation = ValidationUtils.validateConfirmPassword(state.password, state.cnfPassword)
        if (!nameValidation.isValid) {
            onError(nameValidation.errorMessage)
            return
        }
        if (!emailOrPhoneValidation.isValid) {
            onError(emailOrPhoneValidation.errorMessage)
            return
        }
        if (!passwordValidation.isValid) {
            onError(passwordValidation.errorMessage)
            return
        }
        if (!confirmPasswordValidation.isValid) {
            onError(confirmPasswordValidation.errorMessage)
            return
        }
        viewModelScope.launch {
            repository.registerRequest(state.name, state.emailOrPhone, state.cnfPassword, "Android", fcmToken.toString())
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



    fun updatePasswordRequest(email: String, onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val passwordValidation = ValidationUtils.validatePassword(state.password)
        val confirmPasswordValidation = ValidationUtils.validateConfirmPassword(state.password, state.cnfPassword)
        if (!passwordValidation.isValid) {
            onError(passwordValidation.errorMessage)
            return
        }
        if (!confirmPasswordValidation.isValid) {
            onError(confirmPasswordValidation.errorMessage)
            return
        }
        viewModelScope.launch {
            repository.updatePasswordRequest(email, state.cnfPassword)
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



