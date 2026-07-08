package com.bussiness.curemegptapp

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun VoiceInputUI() {

    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("") }
    var rmsValue by remember { mutableStateOf(0f) }
    var textInput by remember { mutableStateOf("") }     // user typing
    var voiceText by remember { mutableStateOf("") }     // speech result


    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    DisposableEffect(Unit) {

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onRmsChanged(rmsdB: Float) {
                rmsValue = rmsdB
            }

            override fun onPartialResults(partialResults: Bundle?) {
                recognizedText =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )?.firstOrNull() ?: ""
            }

            override fun onResults(results: Bundle?) {
                recognizedText =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )?.firstOrNull() ?: ""
                // 🛑 Final stop
//                isRecording = false
//                rmsValue = 0f
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onEndOfSpeech() {
                Log.d("VOICE", "User stopped speaking")

                // ⏹️ Auto stop recording
                isRecording = false

                // Waveform freeze
                rmsValue = 0f

                // Keep waveform visible, text on click
                // (Do NOT clear recognizedText here)
            }

            override fun onError(error: Int) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose { speechRecognizer.destroy() }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {

        // 🔵 TOP BUBBLE (Image-1 style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F1FF), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {



                //   if (!showText) {
                if (recognizedText.isNotEmpty()   && !showText) {

                    Text(
                        text = "See text",
                        color = Color.Gray,
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null) {
                            showText = true
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            painterResource(R.drawable.ic_close),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                                indication = null) {
                                speechRecognizer.cancel()
                                recognizedText = ""
                                rmsValue = 0f
                                isRecording = false
                                showText = false

                            }
                        )

                        Spacer(Modifier.width(12.dp))

                        if (isRecording){
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(R.raw.ic_voice_wave_json)
                            )

                            LottieAnimation(
                                composition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.weight(1f)
                            )
                        }else{
                            Image(
                                painter = painterResource(R.drawable.voice_waveform),
                                contentDescription = null,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Waveform image


                        Spacer(Modifier.width(12.dp))


                    }

                }
                else {
                    if(showText){  voiceText = recognizedText}
//                    OutlinedTextField(
//                        value = textInput + voiceText,
//                        onValueChange = {
//                            textInput = it
//                            recognizedText = "" // user typing overrides voice buffer
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        placeholder = { Text("Type or speak…") },
//                        maxLines = 4
//                    )

                    TextField(
                        value = textInput + voiceText,
                        onValueChange = { textInput = it
                           recognizedText = "" },
                        modifier = Modifier.fillMaxWidth(),
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
                        ))
                }
                /*           //   else {

              //                if (showText) {
              //                    Text(
              //                        text = if (recognizedText.isEmpty())
              //                            "No speech detected"
              //                        else recognizedText,
              //                        color = Color.Black
              //                    )
              //                    }





              //              if (showText){
              //                    OutlinedTextField(
              //                        value = textInput + voiceText,
              //                        onValueChange = {
              //                            textInput = it
              //                            recognizedText = "" // user typing overrides voice buffer
              //                        },
              //                        modifier = Modifier.fillMaxWidth(),
              //                        placeholder = { Text("Type or speak…") },
              //                        maxLines = 4
              //                    )
              //                }

                             // }*/
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🎤 MIC BUTTON

        if (recognizedText.isEmpty() && textInput.isEmpty()) {
            IconButton(
                onClick = {
                    if (!isRecording) {
                        recognizedText = ""
                        showText = false
                        speechRecognizer.startListening(intent)
                        //if (recognizedText.isEmpty()) showText = true else showText = false
                    } else {
                        speechRecognizer.stopListening()
                    }
                    isRecording = !isRecording
                    Log.d("Show Recording", isRecording.toString())
                },
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        if (isRecording) Color.Red else Color(0xFF6A5AE0),
                        CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mic_icon),
                    contentDescription = "Mic",
                    tint = Color.Unspecified
                )
            }
        }
        else {
            IconButton(
                onClick = {
//                    viewModel.sendMessageFromInput()
//                    onSendClicked()
                }
            ) {
                Icon(
                    painterResource(R.drawable.send_ic),
                    contentDescription = "Send",
                    tint = Color.Unspecified,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}
