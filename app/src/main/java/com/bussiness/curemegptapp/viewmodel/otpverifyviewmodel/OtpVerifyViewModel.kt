package com.bussiness.curemegptapp.viewmodel.otpverifyviewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginData
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.ui.uistate.SignUpUiState
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.Messages
import com.bussiness.curemegptapp.util.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerifyViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    private var fcmToken by mutableStateOf<String?>(null)

    init {
        fetchToken()
    }

    fun onOtpChange(value: String) {
        _uiState.value = _uiState.value.copy(otp = value)
    }

    fun forgotOtpRequest(email: String,fromScreen: String, onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val otp = state.otp
        val pleaseEnterOtp = Messages.OTP_ERROR
        val enterCompleteOtp = Messages.OTP_LENGTH_ERROR
        if (otp.isEmpty()) {
            onError(pleaseEnterOtp)
            return
        }
        if (otp.length< 5) {
            onError(enterCompleteOtp)
            return
        }
        viewModelScope.launch {
            repository.otpVerifyRequest( email,state.otp,fcmToken.toString(),fromScreen)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val data = result.data.data
                            data?.let { userData->
                                if (fromScreen.equals("create",true)){
                                    sessionManager.setToken(userData.token?:"")
                                    sessionManager.setUserId(userData.user?.id.toString())
                                    sessionManager.setUserName(userData.user?.name.toString())
                                    sessionManager.setLogin(true)
                                }
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


    fun sendOtpRequest(email: String, onSuccess: (LoginData) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.sendOtpRequest( email)
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

}



