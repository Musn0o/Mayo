package com.musno.mayo.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

object SpeechHelper {

    fun isOfflineAvailable(context: Context): Boolean {
        return SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
    }

    fun startListening(
        context: Context,
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onPartialResult: ((String) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("التعرف على الصوت غير متوفر على هذا الجهاز")
            return
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "ar-SA")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                onReady?.invoke()
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "لم يتم التعرف على الصوت"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "انتهت مهلة التسجيل"
                    SpeechRecognizer.ERROR_AUDIO -> "خطأ في التسجيل الصوتي"
                    SpeechRecognizer.ERROR_CLIENT -> "خطأ في خدمة التسجيل"
                    SpeechRecognizer.ERROR_NETWORK -> "خطأ في الشبكة"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "انتهت مهلة الشبكة"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "خدمة التسجيل مشغولة"
                    else -> "خطأ غير معروف ($error)"
                }
                onError(message)
                recognizer.destroy()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                onResult(text)
                recognizer.destroy()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    onPartialResult?.invoke(text)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer.startListening(intent)
    }

    fun stopListening(recognizer: SpeechRecognizer?) {
        recognizer?.stopListening()
        recognizer?.destroy()
    }
}