package com.bussiness.curemegptapp.ui.viewModel.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.DownloadUtils
import com.bussiness.curemegptapp.util.LoaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// --- Domain models ---

data class FamilyMember(
    val id: String = "",
    val name: String = "",
    val profileImage: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val relation: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val height: String = "",
    val weight: String = "",
    val bloodGroup: String = "",
    val allergies: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val chronicConditions: String = "",
    val surgicalHistory: String = "",
    val currentMedications: List<String> = emptyList(),
    val currentSupplements: List<String> = emptyList(),
    val documents: List<Document> = emptyList()
)

data class Document(
    val id: String = "",
    val fileName: String = "",
    val fileUrl: String = "",
    val fileType: String = ""
)

/**
 * Tracks the download lifecycle of a single document.
 *
 * [Idle]        – no download in progress
 * [Downloading] – download has been enqueued with Android DownloadManager
 * [Success]     – download successfully enqueued (DownloadManager handles the rest)
 * [Error]       – something went wrong before/during enqueue
 */
sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}

@HiltViewModel
class FamilyProfileViewModel @Inject constructor(
    val repository: Repository
) : ViewModel() {

    private val _familyMember = MutableStateFlow<FamilyMember?>(null)
    val familyMember: StateFlow<FamilyMember?> = _familyMember.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Per-document download states keyed by [Document.id].
     * The UI observes this to show a spinner or tick next to each document row.
     */
    private val _downloadStates = MutableStateFlow<Map<String, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadState>> = _downloadStates.asStateFlow()

    // -----------------------------------------------------------------------
    // Load
    // -----------------------------------------------------------------------

    fun loadFamilyMember(memberId: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            getSampleFamilyMember(memberId)
        }
    }

    // -----------------------------------------------------------------------
    // Download
    // -----------------------------------------------------------------------

    /**
     * Downloads [document] to the public Downloads directory using the Android
     * [android.app.DownloadManager] — the industry-standard approach for:
     *   • Background, resumable downloads
     *   • OS-level progress notification in the notification shade
     *   • No extra storage permissions needed on Android 10+
     *
     * The function is intentionally kept free of coroutine IO work because
     * [DownloadManager.enqueue] is non-blocking by design.
     */
    fun downloadDocument(context: Context, document: Document) {
        if (document.fileUrl.isBlank()) {
            _downloadStates.update { it + (document.id to DownloadState.Error("Invalid file URL")) }
            return
        }

        // Mark as "Downloading" so the UI can show a loading indicator
        _downloadStates.update { it + (document.id to DownloadState.Downloading) }

        try {
            DownloadUtils.downloadFile(
                context = context,
                url = document.fileUrl,
                fileName = document.fileName.ifBlank { "document_${document.id}" }
            )
            // DownloadManager.enqueue() succeeded — transition to Success.
            // Actual file-delivery happens asynchronously inside DownloadManager.
            _downloadStates.update { it + (document.id to DownloadState.Success) }
        } catch (e: Exception) {
            _downloadStates.update {
                it + (document.id to DownloadState.Error(e.localizedMessage ?: "Download failed"))
            }
        }
    }

    // -----------------------------------------------------------------------
    // CRUD helpers
    // -----------------------------------------------------------------------

    fun deleteFamilyMember() {
        viewModelScope.launch {
            _familyMember.value = null
        }
    }

    fun addDocument(document: Document) {
        val currentMember = _familyMember.value ?: return
        _familyMember.value = currentMember.copy(
            documents = currentMember.documents + document
        )
    }

    fun removeDocument(documentId: String) {
        val currentMember = _familyMember.value ?: return
        _familyMember.value = currentMember.copy(
            documents = currentMember.documents.filter { it.id != documentId }
        )
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private fun getSampleFamilyMember(id: Int) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.getFamilyMemberProfileDetails(id).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        _familyMember.value = it.data
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteProfile(id: Int, onSucess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteFamilyMember(id).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        onSucess()
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(it.message ?: "Unknown error")
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteFamilyProfilePhoto(id: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteFamilyProfilePhoto(id).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        _familyMember.value = _familyMember.value?.copy(profileImage = "")
                        onSuccess(it.data ?: "Profile photo deleted successfully.")
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                        onError(it.message ?: "Unknown error")
                    }
                    else -> {}
                }
            }
        }
    }

    fun uploadProfilePhoto(
        requestBody: MultipartBody.Part,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val member = _familyMember.value ?: run {
            Log.e("UPDATE_FAMILY_PHOTO", "ViewModel: Family member profile is null!")
            onError("Family member profile is not loaded")
            return
        }
        viewModelScope.launch {
            LoaderManager.show()
            val memberId = member.id
            Log.d("UPDATE_FAMILY_PHOTO", "ViewModel: uploadProfilePhoto called for memberId: $memberId")
            repository.updateFamilyProfilePhoto(
                familyMemberId = memberId.toRequestBody(),
                profileImage = requestBody
            ).collectLatest { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        Log.d("UPDATE_FAMILY_PHOTO", "ViewModel: Successfully uploaded profile photo. Reloading profile details...")
                        LoaderManager.hide()
                        loadFamilyMember(memberId.toIntOrNull() ?: 1)
                        onSuccess()
                    }
                    is NetworkResult.Error -> {
                        val errMsg = result.message ?: "Unknown error"
                        Log.e("UPDATE_FAMILY_PHOTO", "ViewModel: Upload failed. Error: $errMsg")
                        LoaderManager.hide()
                        onError(errMsg)
                    }
                    is NetworkResult.Loading -> {
                        Log.d("UPDATE_FAMILY_PHOTO", "ViewModel: NetworkResult is Loading...")
                    }
                    else -> {}
                }
            }
        }
    }


}