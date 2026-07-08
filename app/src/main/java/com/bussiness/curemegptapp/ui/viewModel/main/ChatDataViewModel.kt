package com.bussiness.curemegptapp.ui.viewModel.main

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri

import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
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
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.apimodel.ChatScreenArgs
import com.bussiness.curemegptapp.apimodel.chatModel.ChatHistoryItem
import com.bussiness.curemegptapp.apimodel.chatModel.FamilyDetails
import com.bussiness.curemegptapp.apimodel.scheduleAppointment.FamilyModel
import com.bussiness.curemegptapp.repository.NetworkResult
import com.bussiness.curemegptapp.repository.Repository
import com.bussiness.curemegptapp.util.DownloadFileHelper
import com.bussiness.curemegptapp.util.LoaderManager
import com.bussiness.curemegptapp.util.UriToRequestBody
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.bussiness.curemegptapp.util.SessionManager


data class ChatInputState1(
    val message: String = "",
    val images: List<Uri> = emptyList(),
    val pdfs: List<PdfData> = emptyList(),
    val isRecording: Boolean = false,
    val showVoicePreview: Boolean = false,
    val transcribedText: String = "",
    val selectedProfile: Profile? = null,
    val showProfileDropdown: Boolean = false
)

@HiltViewModel
class ChatDataViewModel @Inject constructor(
    private val app: Application,
    val repository: Repository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _historyChatList = MutableStateFlow<MutableList<ChatHistoryItem>>(mutableListOf())

    val historyChatList: StateFlow<MutableList<ChatHistoryItem>> = _historyChatList


    private lateinit var context: Context

    private val _chatArgs = MutableStateFlow(ChatScreenArgs())
    val chatArgs: StateFlow<ChatScreenArgs> = _chatArgs

    private val _uiState = MutableStateFlow(ChatInputState1())
    val uiState: StateFlow<ChatInputState1> = _uiState

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages


    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    // For tracking message being edited
    private var editingMessageId: String? = null

    init {

        _profiles.value = getDefaultProfiles()

        if (_profiles.value.isNotEmpty()) {
            _uiState.update { it.copy(selectedProfile = _profiles.value.first()) }
        }

        //  getFamilyMembers()

    }


    fun setChatArgs(
        context: Context,
        chatId: Int,
        familyMemberId: Int,
        chatMessage: ChatMessage?,
        type: String,
        familyList: List<FamilyDetails>
    ) {
        this.context = context

        _chatArgs.value = ChatScreenArgs(
            chatId = chatId,
            familyMemberId = familyMemberId,
            chatMessage = chatMessage,
            type = type,
            familyList = familyList
        )
    }


    fun updateChatType(type: String) {
        _chatArgs.value = _chatArgs.value?.copy(
            type = type
        )!!
    }


    fun switchFamilyMember(context: Context, newMemberId: Int) {
        // Messages clear karo
        _messages.value = emptyList()

        // ChatId reset karo, familyMemberId naya set karo, type same rahe
        _chatArgs.value = _chatArgs.value.copy(
            chatId = 0,
            familyMemberId = newMemberId
        )

        // Input bhi clear karo
        val selectedProfile = _uiState.value.selectedProfile
        _uiState.update { ChatInputState1(selectedProfile = selectedProfile) }
    }

    fun switchNewChat(context: Context,type:String) {
        // Messages clear karo
        _messages.value = emptyList()

        // ChatId reset karo, familyMemberId naya set karo, type same rahe
        _chatArgs.value = _chatArgs.value.copy(
            chatId = 0,
            type =type
        )

        // Input bhi clear karo
        val selectedProfile = _uiState.value.selectedProfile
        _uiState.update { ChatInputState1(selectedProfile = selectedProfile) }
    }

    private fun getDefaultProfiles(): List<Profile> {
        return listOf(
            Profile(
                id = "general",
                name = "General Assistant",
                iconResId = R.drawable.ic_profile,
                description = "General purpose AI assistant"
            ),
            Profile(
                id = "creative",
                name = "Creative Writer",
                iconResId = R.drawable.ic_profile,
                description = "For creative writing and ideas"
            ),

            Profile(
                id = "technical",
                name = "Technical Expert",
                iconResId = R.drawable.ic_profile,
                description = "Technical and coding assistance"
            ),

            Profile(
                id = "academic",
                name = "Academic Helper",
                iconResId = R.drawable.ic_profile,
                description = "Academic writing and research"
            )

        )
    }

    fun toggleProfileDropdown() {
        _uiState.update { it.copy(showProfileDropdown = !it.showProfileDropdown) }
    }

    fun selectProfile(profile: Profile) {
        _uiState.update { it.copy(selectedProfile = profile, showProfileDropdown = false) }
    }

    fun onMessageChange(newText: String) {
        _uiState.update { it.copy(message = newText.take(1000)) }
    }

    fun addImage(uri: Uri) {
        _uiState.update { it.copy(images = it.images + uri) }
    }

    fun clearImages() {
        _uiState.update { it.copy(images = emptyList()) }
    }

    fun clearPdfs() {
        _uiState.update { it.copy(pdfs = emptyList()) }
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

    fun switchChat(chatId:Int, success: (String) -> Unit , error: (String) -> Unit) {
        viewModelScope.launch {
            LoaderManager.show()
              repository.switchChat(chatId).collectLatest {
                  when(it){
                      is NetworkResult.Success -> {
                          LoaderManager.hide()
                          val data = it.data
                          success(data.toString())
                      }
                      is NetworkResult.Error -> {
                          error(it.message.toString())
                          LoaderManager.hide()
                      }
                      is NetworkResult.Loading -> {
                          LoaderManager.hide()
                      }
                  }
              }
        }
    }


    fun stopRecording() {
        _uiState.update { it.copy(isRecording = false) }
    }

     fun sendMessageFromInput() {
         viewModelScope.launch {
             val s = _uiState.value
             if (s.message.isBlank() && s.images.isEmpty() && s.pdfs.isEmpty()) return@launch


             val realImages = s.images.map { uri ->
                 if (uri.scheme == "http" || uri.scheme == "https") {
                     // Server se download karo
                     DownloadFileHelper.downloadFileToUri(
                         context,
                         uri.toString()
                     ) ?: uri
                 } else {
                     uri  // Already local Uri hai
                 }
             }

             val realPdfs = s.pdfs.map { pdf ->
                 if (pdf.uri.scheme == "http" || pdf.uri.scheme == "https") {
                     val downloadedUri = DownloadFileHelper.downloadFileToUri(
                         context,
                         pdf.uri.toString()
                     )
                     pdf.copy(uri = downloadedUri ?: pdf.uri)
                 } else {
                     pdf  // Already local hai
                 }
             }

             val msg = ChatMessage(
                 text = s.message.takeIf {
                     it.isNotBlank()
                 },
                 isUser = true,
                 images = realImages,
                 pdfs = realPdfs
             )

             _messages.update { it + msg }


             _uiState.update { ChatInputState1(selectedProfile = s.selectedProfile) }


             sendInitialChatRequest(
                 s.message.toString(),
                 chatId = _chatArgs.value.chatId,
                 familyMemberId = _chatArgs.value.familyMemberId,
                 type = _chatArgs.value.type,
                 images = s.images,
                 pdfs = s.pdfs
             )
         }
         // If we're editing a message, update it
//        editingMessageId?.let { messageId ->
//            updateMessage(messageId, s.message)
//            editingMessageId = null
//            _uiState.update {
//                ChatInputState1(selectedProfile = s.selectedProfile)
//            }
//            return
//        }

    }

    fun copyMessage(text: String) {
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Chat message", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(app, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun editMessage(messageId: String) {
        val message = _messages.value.find { it.id == messageId }
        message?.let { msg ->

            editingMessageId = messageId

            _uiState.update {
                it.copy(message = msg.text ?: "", images = msg.images, pdfs = msg.pdfs)
            }


        }
    }

    fun regenerateMessage(messageId: String) {
        viewModelScope.launch {
            val index = _messages.value.indexOfLast { it.id == messageId }
            if (index != -1) {
                // Show loading indicator
                _messages.update {
                    it.toMutableList().apply {
                        this[index] = this[index].copy(isGenerating = true)
                    }
                }

                delay(1500)

                _messages.update {
                    it.toMutableList().apply {
                        this[index] = this[index].copy(
                            text = "Here's a regenerated response based on your query.",
                            isGenerating = false
                        )
                    }
                }

                Toast.makeText(app, "Message regenerated", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun rateMessage(messageId: String, isPositive: Boolean) {
        viewModelScope.launch {
            val index = _messages.value.indexOfFirst { it.id == messageId }
            if (index != -1) {
                val current = _messages.value[index]
                val statusStr = if (current.rating == 1 && isPositive) "0"
                else if (current.rating == -1 && !isPositive) "0"
                else if (isPositive) "1"
                else "2"

                repository.responseLikeDislike(messageId, statusStr).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            _messages.update { list ->
                                list.toMutableList().apply {
                                    val idx = indexOfFirst { it.id == messageId }
                                    if (idx != -1) {
                                        val newRating = if (statusStr == "1") 1 else if (statusStr == "2") -1 else 0
                                        this[idx] = this[idx].copy(
                                            rating = newRating,
                                            isRated = statusStr != "0"
                                        )
                                    }
                                }
                            }
                            Toast.makeText(app, result.data ?: "Feedback saved", Toast.LENGTH_SHORT).show()
                        }
                        is NetworkResult.Error -> {
                            Toast.makeText(app, result.message ?: "Failed to save feedback", Toast.LENGTH_SHORT).show()
                        }
                        is NetworkResult.Loading -> {}
                    }
                }
            }
        }
    }


    private fun updateMessage(messageId: String, newText: String) {
        val index = _messages.value.indexOfFirst { it.id == messageId }
        if (index != -1) {
            _messages.update {
                it.toMutableList().apply {
                    val message = this[index]
                    this[index] = message.copy(text = newText)
                }
            }

            Toast.makeText(app, "Message updated", Toast.LENGTH_SHORT).show()

            if (_messages.value[index].isUser) {
                //simulateAIResponse()
            }
        }
    }


    private fun getPdfName(uri: Uri): String {
        return app.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            if (index >= 0) cursor.getString(index) else "document.pdf"
        } ?: "document.pdf"
    }

    fun onVoiceRecorded(text: String) {
        _uiState.update {
            _uiState.value.copy(
                transcribedText = text,
                showVoicePreview = false,
                message = text,
                isRecording = false
            )
        }
    }

    fun onPartialVoiceResult(partialText: String) {
        _uiState.update {
            it.copy(
                message = partialText,  // Update message with partial text
                transcribedText = partialText
            )
        }
    }


    //    fun showTranscribedText() {
//        _uiState.update { _uiState.value.copy(
//            message = _uiState.value.transcribedText,
//            showVoicePreview = false
//        ) }
//    }
    fun showTranscribedText() {
        _uiState.update {
            it.copy(
                message = it.transcribedText,
                showVoicePreview = false
            )
        }
    }

//    fun clearVoicePreview() {
//        _uiState.update { it.copy(showVoicePreview = false, transcribedText = "") }
//    }

    fun clearVoicePreview() {
        _uiState.update {
            it.copy(
                showVoicePreview = false,
                transcribedText = "",
                message = ""  // Also clear message
            )
        }
    }

    fun clearEdit() {
        editingMessageId = null
        val selectedProfile = _uiState.value.selectedProfile
        _uiState.update { ChatInputState1(selectedProfile = selectedProfile) }
    }


    fun onChatScreenOpened(
        chatId: Int,
        type: String,
        message: String?,
        familyMemberId: Int,
         images: List<Uri> = emptyList(),
         pdfs: List<PdfData> = emptyList()
    ) {
        if (chatId == 0 && type == "normal") {
            when {
                images.isNotEmpty() -> {
                    _messages.update { list ->
                        list + ChatMessage(
                            images = images,
                            isUser = true
                        )
                    }
                }

                pdfs.isNotEmpty() -> {
                    _messages.update { list ->
                        list + ChatMessage(
                            pdfs = pdfs,
                            isUser = true
                        )
                    }
                }
            }

            message?.let {
                _messages.update { list ->
                    list + ChatMessage(
                        text = it,
                        isUser = true
                    )
                }
            }

            sendInitialChatRequest(
                message = message,
                chatId = chatId,
                familyMemberId = familyMemberId,
                type = type,
                images = images,
                pdfs = pdfs
            )
        }
    }

    private fun sendInitialChatRequest(
        message: String?,
        chatId: Int,
        familyMemberId: Int,
        type: String,
        images: List<Uri> = emptyList(),
        pdfs: List<PdfData> = emptyList()
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            if (message.isNullOrBlank() && images.isEmpty() && pdfs.isEmpty()) {
                return@launch
            }

            val messageBody = message?.toRequestBody("text/plain".toMediaTypeOrNull())
                ?: return@launch

            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())

            val chatIdBody = if (chatId == 0) null
            else chatId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            Log.d("TESTING_CHAT_ID","CHAT ID VALUE IS"+ chatId)
            val familyBody = if (familyMemberId == 0) null
            else familyMemberId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            var  profileImage : MultipartBody.Part? = null

            if(images.size > 0){

                profileImage = UriToRequestBody.uriToMultipart(context, images.first(), "file")

            }else{
                pdfs.firstOrNull()?.uri?.let { uri ->
                    profileImage = UriToRequestBody.uriToMultipart(
                        context = context,
                        uri = uri,
                        partName = "file"
                    )
                }
            }

            val dummyMsg = ChatMessage(isUser = false, isGenerating = true)
            withContext(Dispatchers.Main) {
                _messages.update { it + dummyMsg }
            }

            repository.getChatResponse(
                familyMemberId = familyBody,
                message = messageBody,
                type = typeBody,
                chatId = chatIdBody,
                profile_image = profileImage
            ).collect { result ->

                when (result) {

                    is NetworkResult.Success -> {
                        withContext(Dispatchers.Main) {
                            _chatArgs.value = _chatArgs.value.copy(
                                chatId = result.data?.chatId?.toInt() ?: 0
                            )
                            _messages.update { currentList ->
                                val listWithoutDummy = currentList.filter { !it.isGenerating }
                                if (result.data != null) {
                                    listWithoutDummy + result.data
                                } else {
                                    listWithoutDummy
                                }
                            }
                            if (_chatArgs.value.type == "case" && result.data?.text != null) {
                                checkForCaseChatSuggestions(result.data.text)
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            _messages.update { currentList ->
                                val listWithoutDummy = currentList.filter { !it.isGenerating }
                                listWithoutDummy + ChatMessage(
                                    text = result.message,
                                    isUser = false
                                )
                            }
                        }
                    }

                    is NetworkResult.Loading -> {

                    }
                }
            }
        }
    }


    fun getChatHistoryData(chatId: Int, type: String = "normal") {
        _chatArgs.value = _chatArgs.value.copy(
            chatId = chatId,
            type = type
        )
        viewModelScope.launch {
            repository.getChatMessage(chatId).collect {
              when(it){
                    is NetworkResult.Success -> {
                        Log.d("TESTING_CHAT_HISTORY","CHAT HISTORY"+ it.data.toString())
                        _messages.value = it.data ?: emptyList()
                        if (type == "case") {
                            it.data?.forEach { msg ->
                                if (!msg.isUser && msg.text != null) {
                                    checkForCaseChatSuggestions(msg.text)
                                }
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Loading -> {
                        // show loading if needed
                    }
              }
            }
        }
    }





    fun renameChat(
        chatId: String,
        title: String,
        success :()->Unit
    ){
        viewModelScope.launch {
            LoaderManager.show()
            repository.renameChat(chatId.toInt(), title).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        success()
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {
                        LoaderManager.hide()
                    }
                }
            }
        }
    }

    fun deleteChat(chatId: String,success: () -> Unit){
        viewModelScope.launch {
            LoaderManager.show()
            repository.deleteChat(chatId.toInt()).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        success()
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {
                        LoaderManager.hide()
                    }
                }
            }
        }
    }

    fun getUserChatHistoryList() {
        viewModelScope.launch {
            LoaderManager.show()
            repository.getUserChatHistoryList().collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        it.data?.let {
                            _historyChatList.value = it
                        }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {
                        LoaderManager.hide()
                    }
                }

            }
        }
    }


    fun getUserFamilyChatList(id:Int){
        viewModelScope.launch {
            LoaderManager.show()
            repository.getUserFamilyMemberChatList(id).collectLatest {
                when (it) {
                    is NetworkResult.Success -> {
                        LoaderManager.hide()
                        it.data?.let {
                            _historyChatList.value = it
                        }
                    }
                    is NetworkResult.Error -> {
                        LoaderManager.hide()
                    }
                    else -> {
                        LoaderManager.hide()
                    }
                }
            }
        }
    }

    fun checkForCaseChatSuggestions(messageText: String) {
        val textLower = messageText.lowercase()
        val hasUrgentKeywords = listOf("urgent", "critical", "severe", "emergency", "pain", "hospital", "doctor", "appointment", "schedule", "dentist", "physician", "visit clinic").any { textLower.contains(it) }
        if (hasUrgentKeywords) {
            val alertTitle = when {
                textLower.contains("tooth") || textLower.contains("teeth") || textLower.contains("gum") || textLower.contains("wisdom") -> "Dental Appointment Recommended"
                textLower.contains("heart") || textLower.contains("chest") -> "Cardiology Consult Required"
                textLower.contains("stomach") || textLower.contains("abdomen") || textLower.contains("belly") -> "Gastroenterology Checkup Suggested"
                textLower.contains("headache") || textLower.contains("dizzy") || textLower.contains("head") -> "Neurology Evaluation Suggested"
                else -> "Urgent Health Concern Evaluation"
            }
            sessionManager.addChatAlert(alertTitle)
        }
    }
}



