package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Manages audio tones, device haptics, and Text-To-Speech pronunciation.
 */
class AudioHapticsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var toneGenerator: ToneGenerator? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            tts = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }

    fun speak(text: String) {
        if (!soundEnabled || !isTtsReady) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_word_${System.currentTimeMillis()}")
        } catch (_: Exception) {
        }
    }

    fun playSnapSound() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        } catch (_: Exception) {
        }
    }

    fun playWordConnectTone() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 60)
        } catch (_: Exception) {
        }
    }

    fun playVictoryFanfare() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 250)
        } catch (_: Exception) {
        }
    }

    fun playErrorBuzz() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
        } catch (_: Exception) {
        }
    }

    fun playHintChime() {
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
        } catch (_: Exception) {
        }
    }

    fun vibrateSnap() {
        if (!hapticsEnabled) return
        try {
            vibrate(30, 180)
        } catch (_: Exception) {
        }
    }

    fun vibrateWordLetter() {
        if (!hapticsEnabled) return
        try {
            vibrate(15, 120)
        } catch (_: Exception) {
        }
    }

    fun vibrateSuccess() {
        if (!hapticsEnabled) return
        try {
            vibrate(80, 255)
        } catch (_: Exception) {
        }
    }

    fun vibrateError() {
        if (!hapticsEnabled) return
        try {
            vibrate(60, 200)
        } catch (_: Exception) {
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                vibratorManager?.defaultVibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                    vibrator?.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (_: Exception) {
        }
    }
}
