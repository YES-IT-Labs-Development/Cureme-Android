package com.bussiness.curemegptapp.ui.viewModel.main

//EditProfileViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.MultipartHelper
import com.bussiness.curemegptapp.util.SessionManager
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var currentOtp = ""
    val currentStep = mutableStateOf(0)
    val uploadedFilesUrl = mutableListOf<String>()

    val profileData = mutableStateOf(ProfileData())

    var profileFormState by mutableStateOf(ProfileData())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun getProfileData(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            LoaderManager.show()
            repository.getPersonalProfile().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val user = result.data
                        val baseUrl = AppConstant.IMAGE_BASE_URL

                        val photoPath = (user?.profile_photo?.takeIf { it.isNotBlank() } ?: user?.profile_image)?.takeIf { it.isNotBlank() } ?: ""
                        val image = if (photoPath.isBlank()) {
                            ""
                        } else if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                            photoPath
                        } else {
                            "$baseUrl$photoPath"
                        }

                        profileFormState = profileFormState.copy(
                            id = user?.id ?: 0,
                            fullName = user?.name ?: "",
                            contactNumber =  user?.phone?.substringAfter(" ") ?: "",
                            email = user?.email ?: "",
                            dateOfBirth = user?.dob ?: "",
                            gender = user?.gender ?: "",
                            height = user?.height ?: "",
                            weight = user?.weight ?: "",
                            profileImage = image,
                            emailVerify = if (!user?.email.isNullOrEmpty()) {
                                    true
                              } else {
                                   false
                             },
                            phoneVerify = if (!user?.phone.isNullOrEmpty()) {
                                    true
                                } else {
                                    false
                                },
                            emailCopy = user?.email ?: "",
                            phoneCopy = user?.phone?:""
                        )
                        isLoading = false
                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        isLoading = false
                        onError(result.message ?: "Something went wrong")
                    }

                    else -> {}
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


    fun onPhoneChange(value: String) {
        profileFormState = profileFormState.copy(phoneCopy = value)
    }

    fun onEmailVerified(){
        profileFormState = profileFormState.copy(emailVerify = true, email = profileFormState.emailCopy)
    }

    fun onPhoneVerified(){
        profileFormState = profileFormState.copy(phoneVerify = true, contactNumber = profileFormState.phoneCopy)
    }

    fun onEmailChange(value: String) {
        profileFormState = profileFormState.copy(emailCopy = value)
    }

    fun getGeneralProfile(onError: (String) -> Unit){
        viewModelScope.launch {
            LoaderManager.show()
                repository.getGeneralProfile().collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoaderManager.hide()
                            val user = result.data
                            Log.d("TESTING_ALLERGIES", "Allergies string: ${user?.allergies}")
                            profileFormState = profileFormState.copy(
                                 bloodGroup = user?.blood_group ?: "Select",
                                 allergies = user?.allergies
                                     ?.split(",")
                                     ?.map { it.trim() } ?: emptyList(),
                                 emergencyContactName = user?.emergency_contact_name ?: "",
                                 emergencyContactPhone = user?.emergency_contact_number ?: ""
                            )

                        }

                        is NetworkResult.Error -> {
                            LoaderManager.hide()
                            onError(result.message ?: "Something went wrong")
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getMedicalHistory(onError: (String) -> Unit){
        viewModelScope.launch {
            LoaderManager.show()
            repository.getGeneralProfileHistory().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val user = result.data
                        profileFormState = profileFormState.copy(
                            chronicConditions = user?.chronic_condition
                                ?.split(",")
                                ?.map { it.trim() } ?: emptyList(),
                            surgicalHistory = user?.surgical_history ?: "",
                            currentMedications = user?.current_medications
                                ?.split(",")
                                ?.map { it.trim() } ?: emptyList(),
                            currentSupplements = user?.current_supplements
                                ?.split(",")
                                ?.map { it.trim() } ?: emptyList()
                        )

                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(result.message ?: "Something went wrong")
                    }

                    else -> {}
                }
            }
        }
    }

    fun getProfileDocuments(onError: (String) ->Unit){
        viewModelScope.launch {
            LoaderManager.show()
            repository.getProfileDocuments().collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val baseUrl = AppConstant.IMAGE_BASE_URL
                        val user = result.data

                        profileFormState = profileFormState.copy(
                            uploadedFiles = user?.map {
                                Uri.parse("$baseUrl$it") } ?: emptyList()
                        ,
                            uploadedDocument = user?:emptyList()

                        )

                    }

                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(result.message ?: "Something went wrong")
                    }

                    else -> {}
                }
            }
        }
    }

    fun updateProfileDocuments(context: Context,onSuccess: () -> Unit, onError: (String) ->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            LoaderManager.show()
            try {
                val parts = mutableListOf<MultipartBody.Part>()
                profileFormState.uploadedFiles.forEach { item ->
                    when (item.scheme) {
                         "http", "https" -> {
                            val part = MultipartHelper.preparePart(context,"documents[]",item)
                            part?.let { parts.add(it) }
                        }
                        "content", "file" -> {
                            val part = UriToRequestBody.uriToMultipart(context,item,"documents[]")
                            part.let { parts.add(it) }
                        }
                        else -> {
                            // Unknown or unsupported scheme
                        }
                    }
                }

                if (parts.isEmpty()) {
                    Log.e("UPLOAD", "No valid parts created")
                    withContext(Dispatchers.Main) {
                        LoaderManager.hide()
                        onSuccess()
                    }
                    return@launch
                }

                repository.updateProfileDocuments(parts).collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoaderManager.hide()
                            Log.d("EditProfileVM", "Documents uploaded successfully")
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        }
                        is NetworkResult.Error -> {
                            LoaderManager.hide()
                            Log.e("EditProfileVM", "Error uploading documents: ${result.message}")
                            withContext(Dispatchers.Main) {
                                onError("Error uploading documents: ${result.message}")
                            }
                        }
                        else -> {

                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Exception uploading documents", e)
                withContext(Dispatchers.Main) {
                    LoaderManager.hide()
                    onError(e.localizedMessage ?: "Unknown error occurred")
                }
            }
        }
    }

    fun completeProfileDocuments(context: Context,onSuccess: () -> Unit, onError: (String) ->Unit){
        viewModelScope.launch(Dispatchers.IO) {
            LoaderManager.show()
            try {
                val parts = mutableListOf<MultipartBody.Part>()

                profileFormState.uploadedFiles.forEach { item ->

                    val part = MultipartHelper.preparePart(
                        context,
                        "documents[]",   // 🔴 backend key
                        item
                    )

                    part?.let { parts.add(it) }
                }

                if (parts.isEmpty()) {
                    Log.e("UPLOAD", "No valid parts created")
                    withContext(Dispatchers.Main) {
                        LoaderManager.hide()
                        onSuccess()
                    }
                    return@launch
                }
                repository.completeProfileDocuments(parts).collectLatest { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoaderManager.hide()
                            Log.d("EditProfileVM", "Documents uploaded successfully")
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        }

                        is NetworkResult.Error -> {
                            LoaderManager.hide()
                            Log.e("EditProfileVM", "Error uploading documents: ${result.message}")
                            withContext(Dispatchers.Main) {
                                onError(result.message ?: "An error occurred")
                            }
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e("EditProfileVM", "Exception uploading documents", e)
                withContext(Dispatchers.Main) {
                    LoaderManager.hide()
                    onError(e.localizedMessage ?: "Unknown error occurred")
                }
            }
        }
    }




    fun updateField(update: ProfileData.() -> ProfileData) {
        profileFormState = profileFormState.update()
    }


    // Functions to update profile data
    fun updatePersonalInfo(
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?,
        profileMultipart: MultipartBody.Part?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {


        viewModelScope.launch {
            repository.updatePersonalProfile(
                name = fullName,
                phone = contactNumber,
                email = email,
                dob = dateOfBirth,
                gender = gender,
                height = height,
                weight = weight,
                profileImage = profileMultipart
            ).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        Log.e("EditProfileVM", "Personal info updated successfully")
                        val current = profileData.value.copy(
                            fullName = fullName,
                            contactNumber = contactNumber,
                            email = email,
                            dateOfBirth = dateOfBirth,
                            gender = gender,
                            height = height,
                            weight = weight,
                            profilePhotoUri = profilePhotoUri
                        )
                        profileData.value = current
                        onSuccess()
                    }

                    is NetworkResult.Error -> {
                        Log.e("EditProfileVM", "Error updating personal info: ${it.message}")
                        onError(it.message ?: "An error occurred")
                    }

                    else -> {}


                }
            }
        }
    }


    fun updateGeneralInfo(
        bloodGroup: String,
        allergies: List<String>,
        emergencyName: String,
        emergencyPhone: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.updateGeneralProfile(
                bloodGroup = bloodGroup,
                allergies = allergies.joinToString(","),
                contactName = emergencyName,
                contactNumber = emergencyPhone
            ).collectLatest {
                when(it){
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            bloodGroup = bloodGroup,
                            allergies = allergies,
                            emergencyContactName = emergencyName,
                            emergencyContactPhone = emergencyPhone
                        )
                        profileData.value = current
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(it.message ?: "An error occurred")
                    }
                    else -> {

                    }
                }
            }
        }
        val current = profileData.value.copy(
            bloodGroup = bloodGroup,
            allergies = allergies,
            emergencyContactName = emergencyName,
            emergencyContactPhone = emergencyPhone
        )
        profileData.value = current
    }

    fun updateMedicalHistory(
        chronicConditions: List<String>,
        surgicalHistory: String,
        currentMedications: List<String>,
        currentSupplements: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {

            LoaderManager.show()

            repository.updateGeneralProfileHistory(
                chronicConditions = chronicConditions.joinToString(","),
                surgicalHistory = surgicalHistory,
                currentMedication = currentMedications.joinToString(","),
                currentSupplement = currentSupplements.joinToString(","),
                ).collectLatest {
                when(it){
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            chronicConditions = chronicConditions,
                            surgicalHistory = surgicalHistory,
                            currentMedications = currentMedications,
                            currentSupplements = currentSupplements
                        )
                        profileData.value = current
                        Log.e("EditProfileVM", "Medical history updated successfully")
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        Log.e("EditProfileVM", "Error updating medical history: ${it.message}")
                        onError(it.message ?: "An error occurred")
                    }
                    else -> {
                    }
                }
            }
        }




    }


    fun addUploadedFile(uri: Uri) {
         val updatedFiles = profileFormState.uploadedFiles.toMutableList()
         updatedFiles.add(uri)
         profileFormState = profileFormState.copy(
                uploadedFiles = updatedFiles
            )
    }


    fun removeUploadedFile(uri: Uri) {
        val updatedFiles = profileFormState.uploadedFiles.toMutableList()
        updatedFiles.remove(uri)

        profileFormState = profileFormState.copy(
            uploadedFiles = updatedFiles
        )
    }

    fun clearUploadedFiles() {
        profileData.value = profileData.value.copy(uploadedFiles = emptyList())
    }

    fun goToNextStep() {
        currentStep.value++
    }

    fun goToPreviousStep() {
        if (currentStep.value > 0) {
            currentStep.value--
        }
    }

    fun submitProfile() {
        // Sab data log karo
        Log.d("PROFILE_DATA", "========== PROFILE COMPLETION DATA ==========")

        // Personal Info
        Log.d("PROFILE_DATA", "PERSONAL INFORMATION:")
        Log.d("PROFILE_DATA", "Full Name: ${profileData.value.fullName}")
        Log.d("PROFILE_DATA", "Contact Number: ${profileData.value.contactNumber}")
        Log.d("PROFILE_DATA", "Email: ${profileData.value.email}")
        Log.d("PROFILE_DATA", "Date of Birth: ${profileData.value.dateOfBirth}")
        Log.d("PROFILE_DATA", "Gender: ${profileData.value.gender}")
        Log.d("PROFILE_DATA", "Height: ${profileData.value.height}")
        Log.d("PROFILE_DATA", "Weight: ${profileData.value.weight}")
        Log.d("PROFILE_DATA", "Profile Photo URI: ${profileData.value.profilePhotoUri}")

        // General Info
        Log.d("PROFILE_DATA", "\nGENERAL INFORMATION:")
        Log.d("PROFILE_DATA", "Blood Group: ${profileData.value.bloodGroup}")
        Log.d("PROFILE_DATA", "Allergies: ${profileData.value.allergies}")
        Log.d("PROFILE_DATA", "Emergency Contact Name: ${profileData.value.emergencyContactName}")
        Log.d("PROFILE_DATA", "Emergency Contact Phone: ${profileData.value.emergencyContactPhone}")

        // Medical History
        Log.d("PROFILE_DATA", "\nMEDICAL HISTORY:")
        Log.d("PROFILE_DATA", "Chronic Conditions: ${profileData.value.chronicConditions}")
        Log.d("PROFILE_DATA", "Surgical History: ${profileData.value.surgicalHistory}")
        Log.d("PROFILE_DATA", "Current Medications: ${profileData.value.currentMedications}")
        Log.d("PROFILE_DATA", "Current Supplements: ${profileData.value.currentSupplements}")

        // Documents
        Log.d("PROFILE_DATA", "\nDOCUMENTS:")
        Log.d("PROFILE_DATA", "Uploaded Files Count: ${profileData.value.uploadedFiles.size}")
        profileData.value.uploadedFiles.forEachIndexed { index, uri ->
            Log.d("PROFILE_DATA", "File ${index + 1}: ${uri.lastPathSegment}")
        }

        Log.d("PROFILE_DATA", "========== END OF PROFILE DATA ==========\n")

        // Yahan aap local storage ya navigation kar sakte hain
        // For example, navigate to home screen
        // navController.navigate("home")
    }
}