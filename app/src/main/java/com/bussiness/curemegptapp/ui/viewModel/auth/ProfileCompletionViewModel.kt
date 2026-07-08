package com.bussiness.curemegptapp.ui.viewModel.auth

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.personalmodel.ProfileResponse
import com.bussiness.curemegptapp.data.model.ProfileData
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.repository.Resource
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileCompletionViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    val currentStep = mutableStateOf(0)
    val profileData = mutableStateOf(ProfileData())

    fun updatePersonalInfo(
        fullName: String,
        contactNumber: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: String,
        weight: String,
        profilePhotoUri: Uri?
    ) {
        val current = profileData.value.copy(
            fullName = fullName,
            contactNumber = contactNumber,
            email = email,
              gender = gender,
            height = height,
            weight = weight,
            profilePhotoUri = profilePhotoUri
        )

        profileData.value = current
    }

    fun updateGeneralInfo(
        onSuccess: () ->Unit,
        onError: (String) -> Unit,
        bloodGroup: String,
        allergies: List<String>,
        emergencyName: String,
        emergencyPhone: String
    ) {
        val allergiesString = allergies.joinToString(",")

       viewModelScope.launch {
           LoaderManager.show()
           repository.generalProfileRequest(bloodGroup,allergiesString,
               emergencyName,emergencyPhone).collect {
                   when(it){
                          is Resource.Success -> {
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
                          is Resource.Error -> {
                              LoaderManager.hide()
                            onError(it.message ?: "An error occurred")
                          }
                          else -> {

                          }
                   }
               }
       }

    }

    fun updateMedicalHistory(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        chronicConditions: List<String>,
        surgicalHistory: String,
        currentMedications: List<String>,
        currentSupplements: List<String>
    ) {


        viewModelScope.launch {
            LoaderManager.show()
            repository.completeGeneralProfileHistoryRequest(
                chronicConditions.joinToString(","),
                surgicalHistory,
                currentMedications.joinToString(","),
                currentSupplements.joinToString(",")
            ).collect {
                when(it){
                    is Resource.Success -> {
                        LoaderManager.hide()
                        val current = profileData.value.copy(
                            chronicConditions = chronicConditions,
                            surgicalHistory = surgicalHistory,
                            currentMedications = currentMedications,
                            currentSupplements = currentSupplements
                        )
                        onSuccess()
                        profileData.value = current
                    }
                    is Resource.Error -> {
                        LoaderManager.hide()
                        Log.e("ProfileCompletionVM", "Error updating medical history: ${it.message}")
                        onError(it.message ?: "An error occurred")
                    }
                    else -> {

                    }
                }
            }

        }

    }

    fun addUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.add(uri)
        profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
    }

    fun removeUploadedFile(uri: Uri) {
        val currentFiles = profileData.value.uploadedFiles.toMutableList()
        currentFiles.remove(uri)
        profileData.value = profileData.value.copy(uploadedFiles = currentFiles)
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

    fun submitProfile(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {

        // Sab data log karo
        Log.d("PROFILE_DATA", "========== PROFILE COMPLETION DATA ==========")
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

        viewModelScope.launch {

            LoaderManager.show()
            val list = UriToRequestBody.uriListToMultipartList( context, profileData.value.uploadedFiles ,   "documents[]")

            repository.completeProfileDocumentsRequest(list).collect {
                when(it){
                    is Resource.Success -> {
                        Log.d("PROFILE_DATA", "Documents uploaded successfully")
                        LoaderManager.hide()
                        onSuccess()
                    }
                    is Resource.Error -> {
                        LoaderManager.hide()
                        Log.e("PROFILE_DATA", "Error uploading documents: ${it.message}")
                        onError(it.message ?: "An error occurred while uploading documents")
                    }
                    else -> {

                    }
                }
            }
        }


        profileData.value.uploadedFiles.forEachIndexed { index, uri ->
            Log.d("PROFILE_DATA", "File ${index + 1}: ${uri.lastPathSegment}")
        }

    }



}