package com.bussiness.curemegptapp.ui.viewModel.main

import android.app.Application
import android.net.Uri

import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.data.model.PdfData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChatInputState(
    val message: String = "",
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList(),
    val isRecording: Boolean = false,
    val showVoicePreview: Boolean = false,   // NEW
    val transcribedText: String = "",         // NEW
    val selectedProfile: Profile? = null,
    val showProfileDropdown: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatInputState())
    val uiState: StateFlow<ChatInputState> = _uiState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages


    fun appendVoiceText(voiceText: String) {
        _uiState.update {
            it.copy(
                message = (it.message + " " + voiceText).trim()
            )
        }
    }

    fun onMessageChange(newText: String) {
        _uiState.update { it.copy(message = newText.take(1000)) }
    }

    fun clearImages() {
        _uiState.update { currentState ->
            currentState.copy(images = emptyList())
        }
    }

    fun clearPdfs() {
        _uiState.update { currentState ->
            currentState.copy(pdfs = emptyList())
        }
    }

    fun addImage(uri: Uri) {
        _uiState.update { it.copy(images = it.images + uri) }
    }

    fun removeImage(uri: Uri) {
        _uiState.update { it.copy(images = it.images - uri) }
    }

    fun addPdf(uri: Uri) {
        val name = getPdfName(uri)
        _uiState.update { it.copy(pdfs = it.pdfs + PdfData(uri, name)) }
    }

    fun removePdf(pdf: PdfData) {
        _uiState.update { it.copy(pdfs = it.pdfs - pdf) }
    }

    fun toggleRecording() {
        _uiState.update { it.copy(isRecording = !it.isRecording) }
    }

    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false) }
    }

    fun sendMessageFromInput() {
        val s = _uiState.value
        if (s.message.isBlank() && s.images.isEmpty() && s.pdfs.isEmpty()) return

        val msg = ChatMessage(
            text = s.message.takeIf { it.isNotBlank() },
            isUser = true,
            images = s.images,
            pdfs = s.pdfs
        )
        _messages.update { it + msg }

        // reset input
        _uiState.update { ChatInputState() }


        viewModelScope.launch {
            delay(800)
            _messages.update {
                it + ChatMessage(text = "Thanks — I got that. Here's a sample reply.", isUser = false)
            }
        }
    }

    // Helper to read filename from content resolver
    private fun getPdfName(uri: Uri): String {
        return app.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (index >= 0) cursor.getString(index) else "document.pdf"
        } ?: "document.pdf"
    }
    fun onVoiceRecorded(text: String) {
        _uiState.value = _uiState.value.copy(
            transcribedText = text,
            showVoicePreview = true,
            message = ""   // input box empty rahe
        )
    }
    fun showTranscribedText() {
        _uiState.value = _uiState.value.copy(
            message = _uiState.value.transcribedText,
            showVoicePreview = false
        )
    }

    fun clearVoicePreview() {
        _uiState.update { it.copy(showVoicePreview = false, transcribedText = "") }
    }

}

// Create Profile.kt in your data/model folder
data class Profile(
    val id: String,
    val name: String,
    val iconResId: Int,
    val description: String
)

