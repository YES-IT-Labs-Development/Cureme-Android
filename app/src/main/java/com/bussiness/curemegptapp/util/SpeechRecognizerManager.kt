package com.bussiness.curemegptapp.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import timber.log.Timber // या Log का use करें

/*
class SpeechRecognizerManager(
    private val context: Context,
    private val onPartialResult: (String) -> Unit,
    private val onFinalResult: (String) -> Unit,
    private val onError: (String) -> Unit
) : RecognitionListener
{

    private var recognizer: SpeechRecognizer? = null

    fun startListening() {
        stopListening()
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("STT not available")
            return
        }
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(this@SpeechRecognizerManager)
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer?.startListening(intent)
    }

    fun stopListening() {
        try {
            recognizer?.stopListening()
            recognizer?.cancel()
            recognizer?.destroy()
        } catch (t: Throwable) {
        }
        recognizer = null
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onPartialResults(partialResults: Bundle?) {
        val list = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        list?.firstOrNull()?.let { onPartialResult(it) }
    }

    override fun onResults(results: Bundle?) {
        val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        list?.firstOrNull()?.let { onFinalResult(it) }
        stopListening()
    }

    override fun onError(error: Int) {
        onError("Speech error: $error")
        stopListening()
    }
}

 */






class SpeechRecognizerManager(
    private val context: Context,
    private val onPartialResult: (String) -> Unit,
    private val onFinalResult: (String) -> Unit,
    private val onError: (String) -> Unit
) : RecognitionListener {

    private var recognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening() {
        if (isListening) {
            stopListening()
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available")
            return
        }

        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@SpeechRecognizerManager)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // या आपकी language
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            }

            recognizer?.startListening(intent)
            isListening = true
            Timber.d("SpeechRecognizer: Started listening")

        } catch (e: Exception) {
            onError("Failed to start listening: ${e.message}")
            isListening = false
        }
    }

    fun stopListening() {
        try {
            if (isListening) {
                recognizer?.stopListening()
                Timber.d("SpeechRecognizer: Stopped listening")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error stopping recognizer")
        }

        try {
            recognizer?.cancel()
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling recognizer")
        }

        try {
            recognizer?.destroy()
        } catch (e: Exception) {
            Timber.e(e, "Error destroying recognizer")
        }

        recognizer = null
        isListening = false
        Timber.d("SpeechRecognizer: Completely stopped")
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Timber.d("SpeechRecognizer: Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Timber.d("SpeechRecognizer: Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Audio level changes
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        Timber.d("SpeechRecognizer: End of speech")
        isListening = false
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onPartialResults(partialResults: Bundle?) {
        val list = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        list?.firstOrNull()?.let {
            Timber.d("SpeechRecognizer: Partial result: $it")
            onPartialResult(it)
        }
    }

    override fun onResults(results: Bundle?) {
        val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        list?.firstOrNull()?.let {
            Timber.d("SpeechRecognizer: Final result: $it")
            onFinalResult(it)
        }
        stopListening()
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error: $error"
        }

        Timber.e("SpeechRecognizer error: $errorMessage")
        onError(errorMessage)
        stopListening()
    }
}
