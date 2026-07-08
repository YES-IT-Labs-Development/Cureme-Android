package com.bussiness.curemegptapp.ui.viewModel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.apimodel.profilemodel.Data
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlin.collections.plus

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _familyMember = MutableStateFlow<FamilyMember?>(null)
    val familyMember: StateFlow<FamilyMember?> = _familyMember.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        userProfileDetail()
    }

    fun loadFamilyMember(memberId: String = "1") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val member = getSampleFamilyMember()
                _familyMember.value = member
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateFamilyMember(updatedMember: FamilyMember) {
        viewModelScope.launch {
            _familyMember.value = updatedMember
            // यहाँ आप data को repository में save कर सकते हैं
        }
    }

    fun deleteFamilyMember() {
        viewModelScope.launch {
            // Delete logic here
            _familyMember.value = null
        }
    }

    fun addDocument(document: Document) {
        val currentMember = _familyMember.value
        if (currentMember != null) {
            val updatedDocuments = currentMember.documents + document
            val updatedMember = currentMember.copy(documents = updatedDocuments)
            _familyMember.value = updatedMember
        }
    }

    fun removeDocument(documentId: String) {
        val currentMember = _familyMember.value
        if (currentMember != null) {
            val updatedDocuments = currentMember.documents.filter { it.id != documentId }
            val updatedMember = currentMember.copy(documents = updatedDocuments)
            _familyMember.value = updatedMember
        }
    }

    private fun getSampleFamilyMember(): FamilyMember {
        return FamilyMember(
            id = "1",
            name = "Rose Logan",
            profileImage = "profile_image_url",
            contactNumber = "+1 555 987 654",
            email = "rosy@gmail.com",
            relation = "Spouse",
            dateOfBirth = "05/08/1995",
            gender = "Female",
            height = "150 Cm",
            weight = "55 Kg",
            bloodGroup = "O+",
            allergies = "Nuts",
            emergencyContact = "--",
            emergencyPhone = "--",
            chronicConditions = "Hypertension",
            surgicalHistory = "--",
            currentMedications = listOf("--", "--", "--", "--"),
            currentSupplements = listOf("--", "--", "--"),
            documents = listOf(
                Document(
                    id = "doc1",
                    fileName = "Demo_1.Pdf",
                    fileUrl = "",
                    fileType = "pdf"
                )
            )
        )
    }

    fun userProfileDetail(){
        viewModelScope.launch {
            LoaderManager.show()
            _isLoading.value = true
            repository.getUserDetails().collectLatest {
                when(it){
                    is NetworkResult.Success -> {
                        val userDetails = it.data
                        LoaderManager.hide()
                        _isLoading.value = false
                        userDetails?.let {
                            _familyMember.value = getProfileUpdate(userDetails)
                        }

                    }
                    is NetworkResult.Error -> {
                        val errorMessage = it.message ?: "Unknown error"
                        LoaderManager.hide()
                        _isLoading.value = false
                        _errorMessage.value = errorMessage
                    }
                    is NetworkResult.Loading -> {
                        // Optionally handle loading state
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun uploadProfilePhoto(requestBody : MultipartBody.Part,onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            LoaderManager.show()
            repository.updateProfilePicture(requestBody).collectLatest {
                when(it){
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        val errorMessage = it.message ?: "Unknown error"
                        LoaderManager.hide()
                        onError(errorMessage)
                    }
                    is NetworkResult.Loading -> {
                        // Optionally handle loading state
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun deleteProfilePhoto(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteProfilePhoto().collectLatest {
                when(it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        val errorMessage = it.message ?: "Unknown error"
                        LoaderManager.hide()
                        onError(errorMessage)
                    }
                    is NetworkResult.Loading -> {
                        // Optionally handle loading state
                    }
                    else -> {}
                }
            }
        }
    }

    fun getProfileUpdate(user: Data.UserProfile) : FamilyMember{

        return FamilyMember(
            id = user.id?.toString().orEmpty(),
            name = user.name.orEmpty(),
            profileImage = AppConstant.IMAGE_BASE_URL + user.profile_image.orEmpty(),
            contactNumber = user.phone.orEmpty(),
            email = user.email.orEmpty(),
            relation = "",
            dateOfBirth = user.dob.orEmpty(),
            gender = user.gender.orEmpty(),
            height = user.height.orEmpty(),
            weight = user.weight.orEmpty(),
            bloodGroup = user.blood_group.orEmpty(),
            allergies = user.allergies.orEmpty(),
            emergencyContact = user.emergency_contact_name.orEmpty(),
            emergencyPhone = user.emergency_contact_number.orEmpty(),
            chronicConditions = user.chronic_condition.orEmpty(),
            surgicalHistory = user.surgical_history.orEmpty(),

            currentMedications = user.current_medications
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),

            currentSupplements = user.current_supplements
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),

            documents = user.medical_documents
                ?.mapIndexed { index, doc ->
                    Document(
                        id = "doc$index",
                        fileName = doc.name.orEmpty(),
                        fileUrl = AppConstant.IMAGE_BASE_URL + doc.path.orEmpty(),
                        fileType = doc.name?.substringAfterLast(".", "") ?: ""
                    )
                }
                ?: emptyList()
        )
    }

}