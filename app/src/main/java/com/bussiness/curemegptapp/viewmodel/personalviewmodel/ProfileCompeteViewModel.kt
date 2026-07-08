package com.bussiness.curemegptapp.viewmodel.personalviewmodel
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.loginmodel.LoginData
import com.bussiness.curemegptapp.apimodel.personalmodel.User
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.ui.uistate.PersonalUiState
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.util.UriToRequestBody.uriToMultipart
import com.bussiness.curemegptapp.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ProfileCompeteViewModel @Inject constructor(private val repository: Repository,
                                                       private val sessionManager: SessionManager) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState = _uiState.asStateFlow()
    var currentOtp = ""
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onPhoneChange(value: String) {
    //    _uiState.value = _uiState.value.copy(phone = value)
          _uiState.value = _uiState.value.copy(phoneCopy = value)
    }

    fun onEmailVerified(){
        _uiState.value = _uiState.value.copy(emailVerify = true)
        _uiState.value =_uiState.value.copy(email = _uiState.value.emailCopy)
    }

    fun onPhoneVerified(){
        _uiState.value = _uiState.value.copy(phoneVerify = true)
        _uiState.value =_uiState.value.copy(phone = _uiState.value.phoneCopy)
    }

    fun onEmailChange(value: String) {
     //   _uiState.value = _uiState.value.copy(email = value)
        _uiState.value =_uiState.value.copy(emailCopy = value)
    }

    fun onDobChange(value: String) {
        _uiState.value = _uiState.value.copy(dob = value)
    }

    fun onGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(gender = value)
    }

    fun onHeightChange(value: String) {
        _uiState.value = _uiState.value.copy(height = value)
    }

    fun onHeightTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(heightType = value)
    }

    fun onWeightChange(value: String) {
        _uiState.value = _uiState.value.copy(weight = value)
    }

    fun onWeightTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(weightType = value)
    }

    fun onImageChange(value: String) {
        _uiState.value = _uiState.value.copy(imageProfile = value)
    }

    fun onImagePathChange(value: String) {
        _uiState.value = _uiState.value.copy(imageProfilePath = value)
    }

    fun getPersonalRequest(onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.profileRequest()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val data = result.data.data?.user
                            data?.let { userData->
                                _uiState.value = _uiState.value.copy(
                                    name = userData.name ?: "",
                                    phone = userData.phone ?: "",
                                    email = userData.email ?: "",
                                    dob = userData.dob ?: "",
                                    gender = userData.gender ?: "Male",
                                    height = userData.height ?: "",
                                    heightType = "Cm",
                                    weight = userData.weight ?: "",
                                    weightType = "Kg",
                                    emailCopy =  userData.email ?: "",
                                    phoneCopy = userData.phone ?: "",
                                    emailVerify = !userData.email.isNullOrEmpty(),
                                    phoneVerify = !userData.phone.isNullOrEmpty()
                                )
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


    fun verifyEmailPhoneRequest(onSuccess: (otpValue:String) -> Unit,onError: (String) -> Unit,
                                emailOrPhone: String){
        viewModelScope.launch {
            LoaderManager.show()
            repository.verifyEmailPhoneRequest(emailOrPhone).collect {
                when(it){

                    is NetworkResult.Success ->{
                        LoaderManager.hide()
                        currentOtp = it.data.toString()
                        onSuccess(currentOtp)
                    }
                    is NetworkResult.Error ->{
                        LoaderManager.hide()
                        onError(it.message.toString())
                    }
                    else ->{

                    }
                }
            }
        }

    }


    fun sendOtpRequest(onSuccess: (LoginData) -> Unit, onError: (String) -> Unit,value:String) {

        val validation = ValidationUtils.validateEmailOrPhone(value)
        if(!validation.isValid) {
            onError(validation.errorMessage)
            return
        }
        viewModelScope.launch {
            repository.forgotOtpRequest( value )
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

    fun verifyOtpRequest(value: String, otp: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.otpVerifyRequest(value, otp, "", "profile")
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            // Check if it's email or phone and update state accordingly
                            if (value.contains("@")) {
                                _uiState.value = _uiState.value.copy(email = value)
                                _uiState.value = _uiState.value.copy(emailVerify = true)
                            } else {
                                _uiState.value = _uiState.value.copy(phone = value)
                                _uiState.value = _uiState.value.copy(phoneVerify = true)
                            }
                            onSuccess()
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





    fun updatePersonalRequest(context: Context, onSuccess: (User) -> Unit, onError: (String) -> Unit){
        val state = _uiState.value
        val nameValidation = ValidationUtils.validateName(state.name)
        val phoneValidation = ValidationUtils.validatePhone(state.phone)
        val emailValidation = ValidationUtils.validateEmail(state.email)
        val dobValidation = ValidationUtils.validateDateOfBirth(state.dob)
        val heightValidation = ValidationUtils.validateHeight(state.height)
        val weightValidation = ValidationUtils.validateWeight(state.weight)
        if (!nameValidation.isValid) {
            onError(nameValidation.errorMessage)
            return
        }
//        if (!phoneValidation.isValid) {
//            onError(phoneValidation.errorMessage)
//            return
//        }
//        if (!emailValidation.isValid) {
//            onError(emailValidation.errorMessage)
//            return
//        }
        if (!dobValidation.isValid) {
            onError(dobValidation.errorMessage)
            return
        }
        if (!heightValidation.isValid) {
            onError(heightValidation.errorMessage)
            return
        }

        if (!weightValidation.isValid) {
            onError(weightValidation.errorMessage)
            return
        }
        if(!_uiState.value.emailVerify){
            onError("Please verify your email before updating profile")
            return
        }
        if (_uiState.value.phoneCopy.isNotBlank() && !_uiState.value.phoneVerify) {
            onError("Please verify your phone before updating profile")
            return
        }

        state.email = _uiState.value.emailCopy
        state.phone =_uiState.value.phoneCopy

        val imagePath = state.imageProfilePath
        val part = if (!imagePath.isNullOrEmpty()) {
            val uri = Uri.parse(imagePath)
            uriToMultipart(context, uri, "profile_image")
        } else {
            null
        }

        viewModelScope.launch {
            repository.updatePersonalRequest(state.name,
                state.phone.substringAfter("+91"),
                state.email,state.dob,state.gender,
                state.height,state.heightType,
                state.weight,
                state.weightType,part)
                .collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            LoaderManager.show()
                        }
                        is Resource.Success -> {
                            LoaderManager.hide()
                            val data = result.data.data?.user
                            data?.let { userData->
                                _uiState.value = _uiState.value.copy(
                                    name = userData.name ?: "",
                                    phone = userData.phone ?: "",
                                    email = userData.email ?: "",
                                    dob = userData.dob ?: "",
                                    gender = userData.gender ?: "Male",
                                    height = userData.height ?: "",
                                    heightType = "Cm",
                                    weight = userData.weight ?: "",
                                    weightType = "Kg",

                                )
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



