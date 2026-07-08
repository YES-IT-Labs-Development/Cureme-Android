package com.bussiness.curemegptapp.ui.component

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.ChatMessage
import com.bussiness.curemegptapp.ui.viewModel.main.ChatDataViewModel
import com.bussiness.curemegptapp.ui.viewModel.main.ChatInputState
import com.bussiness.curemegptapp.ui.viewModel.main.ChatInputState1
import com.bussiness.curemegptapp.util.SpeechRecognizerManager
import com.bussiness.curemegptapp.ui.viewModel.main.ChatViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


@Composable
fun BottomMessageBar1(
    modifier: Modifier = Modifier,
    state: ChatInputState = ChatInputState(),
    viewModel: ChatViewModel = hiltViewModel(),
    onSendClicked: (ChatMessage) -> Unit = {},
    isVisible: Boolean = false
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    // FILE PICKER
    val fileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it)

            if (mimeType?.startsWith("image/") == true) {
                viewModel.clearImages()
                viewModel.addImage(it)
            } else if (mimeType == "application/pdf") {
                viewModel.clearPdfs()
                viewModel.addPdf(it)
            }
        }
    }

    // STATES
    var isRecording by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("") }

    // SPEECH
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    // PERMISSION
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            recognizedText = ""
            showText = false
            speechRecognizer.startListening(intent)
        }
    }

    // LISTENER
    DisposableEffect(Unit) {

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                isRecording = true
            }

            override fun onBeginningOfSpeech() {
                isRecording = true
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )?.firstOrNull() ?: ""

                recognizedText = partial
                viewModel.onMessageChange(partial) // 🔥 LIVE UPDATE


            }

            override fun onResults(results: Bundle?) {
                val voiceResult =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )?.firstOrNull() ?: ""

                recognizedText = voiceResult
                viewModel.onMessageChange(voiceResult)

                isRecording = false
                showText = true
            }

            override fun onEndOfSpeech() {
                isRecording = false
                showText = true

                // 🔥 FALLBACK
                if (recognizedText.isNotBlank()) {
                    viewModel.onMessageChange(recognizedText)
                }
            }

            override fun onError(error: Int) {
                isRecording = false
                showText = true
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose { speechRecognizer.destroy() }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 5.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.width(5.dp))

            val attachIconAlignment =
                if (state.images.isNotEmpty() || state.pdfs.isNotEmpty()) {
                    Alignment.Bottom
                } else {
                    Alignment.CenterVertically
                }

            val shape = if (state.images.isNotEmpty() || state.pdfs.isNotEmpty())
                RoundedCornerShape(20.dp)
            else CircleShape

            val padding = if (state.images.isNotEmpty() || state.pdfs.isNotEmpty())
                12.dp else 0.dp

            Row(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .background(Color(0xFFF5F0FF), shape)
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ATTACH ICON
                Icon(
                    painter = painterResource(id = R.drawable.attach_ic),
                    contentDescription = "Attach File",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .align(attachIconAlignment)
                        .padding(bottom = padding)
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            fileLauncher.launch(
                                arrayOf("image/*", "application/pdf")
                            )
                        }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {

                    // ATTACHMENTS PREVIEW
                    if (state.images.isNotEmpty() || state.pdfs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        InlineAttachmentPreview(
                            images = state.images,
                            pdfs = state.pdfs,
                            onRemoveImage = viewModel::removeImage,
                            onRemovePdf = viewModel::removePdf
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // LISTENING UI (UNCHANGED)
                    if (!showText) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4EFFF), RoundedCornerShape(20.dp))
                                .padding(10.dp)
                        ) {

                            Text(
                                text = "See text",
                                color = Color(0xFF5B47DB),
                                fontSize = 12.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        showText = true
                                    }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                // ❌ CANCEL BUTTON
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Cancel",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(23.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            speechRecognizer.stopListening()
                                            isRecording = false
                                            showText = true
                                            recognizedText = ""
                                        }
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                if (isRecording) {
                                    val composition by rememberLottieComposition(
                                        LottieCompositionSpec.RawRes(R.raw.ic_voice_wave_json)
                                    )

                                    LottieAnimation(
                                        composition,
                                        iterations = LottieConstants.IterateForever,
                                        modifier = Modifier.weight(1f).height(40.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.voice_waveform),
                                        contentDescription = null,
                                        modifier = Modifier.weight(1f).height(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }

                    } else {

                        TextField(
                            value = state.message,
                            onValueChange = {
                                viewModel.onMessageChange(it)
                                recognizedText = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontSize = 13.sp,
                                color = Color.Black
                            ),
                            placeholder = {
                                Text(
                                    "Ask anything…",
                                    fontSize = 12.sp,
                                    color = Color(0xFF949494),
                                    fontFamily = FontFamily(Font(R.font.urbanist_regular))
                                )
                            },
                            maxLines = 4,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            val isMessageEmpty =
                state.message.isBlank() &&
                        recognizedText.isBlank() &&
                        state.images.isEmpty() &&
                        state.pdfs.isEmpty()

            if (isMessageEmpty) {

                // 🎤 MIC BUTTON
                IconButton(
                    onClick = {
                        if (!isRecording) {

                            val permission = ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.RECORD_AUDIO
                            )

                            if (permission == PackageManager.PERMISSION_GRANTED) {
                                recognizedText = ""
                                showText = false
                                speechRecognizer.startListening(intent)
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            }

                        } else {
                            speechRecognizer.stopListening()

                            isRecording = false
                            showText = true

                            // 🔥 FORCE FINAL TEXT
                            if (recognizedText.isNotBlank()) {
                                viewModel.onMessageChange(recognizedText)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.voiceinc_ic),
                        contentDescription = "Voice Input",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(60.dp)
                    )
                }

            } else {

                // 📤 SEND BUTTON
                IconButton(
                    onClick = {

                        val s = viewModel.uiState.value

                        val message = ChatMessage(
                            text = if (s.message.isNotBlank()) s.message else recognizedText,
                            isUser = true,
                            images = s.images,
                            pdfs = s.pdfs
                        )

                        viewModel.sendMessageFromInput()
                        keyboardController?.hide()

                        onSendClicked(message)

                        recognizedText = ""
                        showText = true
                    },
                    modifier = Modifier
                        .size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send_ic),
                        contentDescription = "Send",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}


//@Composable
//fun BottomMessageBar1(
//    modifier: Modifier = Modifier,
//    state: ChatInputState= ChatInputState(),
//    viewModel: ChatViewModel = hiltViewModel(),
//    onSendClicked: () -> Unit = {}
//) {
//    val context = LocalContext.current
//
//    // Pickers
//    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let { viewModel.addImage(it) }
//    }
//    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let { viewModel.addPdf(it) }
//    }
//
//    // Permission launcher for RECORD_AUDIO
//    val recordPermissionLauncher = rememberLauncherForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        if (granted) {
//            // nothing here; clicking mic will handle starting
//        } else {
//            // show explanation or snackbar as needed
//        }
//    }
//
//    // Speech recognizer manager (keeps simple lifecycle inside compose)
////    var speechMgr by remember { mutableStateOf<SpeechRecognizerManager?>(null) }
////
////    DisposableEffect(key1 = context) {
////        onDispose {
////            speechMgr?.stopListening()
////            speechMgr = null
////        }
////    }
//    // Speech recognizer manager (keeps simple lifecycle inside compose)
//    var speechMgr by remember { mutableStateOf<SpeechRecognizerManager?>(null) }
//
//    DisposableEffect(key1 = context) {
//        onDispose {
//            speechMgr?.stopListening()
//            speechMgr = null
//        }
//    }
//
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 5.dp, vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        // Rounded text box
//        Row(
//            modifier = Modifier
//                .weight(1f)
//                .wrapContentHeight()
//                .background(Color(0xFFF5F0FF), RoundedCornerShape(28.dp))
//                .padding(horizontal = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.attach_ic), // your clip icon
//                contentDescription = null,
//                tint = Color.Unspecified,
//                modifier = Modifier
//                    .wrapContentSize()
//                    .clickable ( interactionSource = remember { MutableInteractionSource() },
//                        indication = null){ /*onAttachClick()*/ }
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column {
//                if (state.images.isNotEmpty() || state.pdfs.isNotEmpty()) {
//                    InlineAttachmentPreview(
//                        images = state.images,
//                        pdfs = state.pdfs,
//                        onRemoveImage = viewModel::removeImage,
//                        onRemovePdf = viewModel::removePdf
//                    )
//                    Spacer(modifier = Modifier.height(6.dp))
//                }
//
//                if (state.showVoicePreview) {
//                    VoicePreviewCard(
//                        transcription = state.transcribedText,
//                        onSeeText = { viewModel.showTranscribedText() },
//                        onClose = { viewModel.clearVoicePreview() }
//                    )
//                    Spacer(Modifier.height(8.dp))
//                }else {
//                    TextField(
//                        value = state.message,
//                        onValueChange = { viewModel.onMessageChange(it) },
//                        modifier = Modifier.fillMaxWidth(),
//                        placeholder = { Text("Ask anything…", fontSize = 10.sp) },
//                        maxLines = 4,
//                        colors = TextFieldDefaults.colors(
//                            focusedIndicatorColor = Color.Transparent,
//                            unfocusedIndicatorColor = Color.Transparent,
//                            focusedContainerColor = Color.Transparent,
//                            unfocusedContainerColor = Color.Transparent
//                        )
//                    )
//                }
//
//
//
//            }
//        }
//
//        Spacer(modifier = Modifier.width(6.dp))
//
//        // Voice Button
//        val canSend = state.message.isNotBlank() || state.images.isNotEmpty() || state.pdfs.isNotEmpty()
//
//        AnimatedContent(targetState = canSend) { sending ->
//            if (sending) {
//                IconButton(onClick = { viewModel.sendMessageFromInput()
//                    onSendClicked()}) {
//                    Icon(painterResource(R.drawable.send_ic), contentDescription = "Send", tint = Color.Unspecified, modifier = Modifier.wrapContentSize())
//                }
//            } else {
//                IconButton(
//                    onClick = {
//                        // request permission if needed then start
//                        val hasPermission = ContextCompat.checkSelfPermission(
//                            context,
//                            Manifest.permission.RECORD_AUDIO
//                        ) == PackageManager.PERMISSION_GRANTED
//                        if (!hasPermission) {
//                            recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//                            return@IconButton
//                        }
//
//                        if (!state.isRecording) {
//                            // create manager and start
//                            speechMgr = SpeechRecognizerManager(
//                                context = context,
//                                onPartialResult = { partial ->
//                                    // show partial transcription in input
//                                    viewModel.onMessageChange(partial)
//                                },
//                                onFinalResult = { final ->
//                                    viewModel.onVoiceRecorded(final)
//                                },
//                                onError = { error ->
//                                    // log or show snackbar
//                                }
//                            ).also { it.startListening() }
//                            viewModel.toggleRecording()
//                        } else {
//                            // stop
//                            speechMgr?.stopListening()
//                            viewModel.stopRecording()
//                        }
//                    }
//                ) {
//                    Icon(
//                        painterResource(R.drawable.voiceinc_ic),
//                        contentDescription = "Voice Input",
//                        tint = Color.Unspecified,
//                        modifier = Modifier.wrapContentSize()
//                    )
//                }
////                IconButton(onClick = {
////                    // request permission if needed then start
////                    val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
////                    if (!hasPermission) {
////                        recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
////                        return@IconButton
////                    }
////
////                    if (!state.isRecording) {
////                        // create manager and start
////                        speechMgr = SpeechRecognizerManager(
////                            context = context,
////                            onPartialResult = { partial ->
////                                // show partial transcription in input
////                                viewModel.onMessageChange(partial)
////                            },
////                            onFinalResult = { final ->
////                                viewModel.onMessageChange(final)
////                                viewModel.stopRecording()
////                                // optionally auto-send
////                                // viewModel.sendMessageFromInput()
////                            },
////                            onError = { error ->
////                                // log or show snackbar
////                            }
////                        ).also { it.startListening() }
////                        viewModel.toggleRecording()
////                    } else {
////                        // stop
////                        speechMgr?.stopListening()
////                        viewModel.stopRecording()
////                    }
////                }) {
////                    if (state.isRecording) {
//////                                Image(painter = painterResource(R.drawable.ic_mic_off), contentDescription = "Mic", modifier = Modifier.size(24.dp))
////                    } else {
//////                                Icon(Icons.Default.Mic, contentDescription = "Mic")
////                    }
////                }
//
//            }
//        }
//    }
//}

@Composable
fun VoicePreviewCard(
    transcription: String,
    onSeeText: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4EFFF), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {

        // SEE TEXT
        Text(
            "See text",
            fontSize = 10.sp,
            color = Color(0xFF4338CA),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onSeeText() }
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painterResource(R.drawable.ic_close),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClose() }
            )

            Spacer(Modifier.width(12.dp))

            // Waveform image
            Image(
                painter = painterResource(R.drawable.voice_waveform),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))


        }
    }
}
