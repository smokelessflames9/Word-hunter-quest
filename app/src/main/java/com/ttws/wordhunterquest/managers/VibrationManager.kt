package com.ttws.wordhunterquest.managers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibrationManager(
    private val context: Context,
    private val progressManager: ProgressManager
) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    enum class VibeType {
        LIGHT_TICK,
        WORD_FOUND,
        INCORRECT,
        HINT,
        LEVEL_COMPLETE
    }

    fun vibrate(type: VibeType) {
        if (!progressManager.isVibrationEnabled()) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    VibeType.LIGHT_TICK -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibeType.WORD_FOUND -> VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 70), intArrayOf(0, 180, 0, 255), -1)
                    VibeType.INCORRECT -> VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibeType.HINT -> VibrationEffect.createOneShot(50, 150)
                    VibeType.LEVEL_COMPLETE -> VibrationEffect.createWaveform(longArrayOf(0, 60, 50, 60, 50, 140), intArrayOf(0, 150, 0, 180, 0, 255), -1)
                }
                v.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    VibeType.LIGHT_TICK -> v.vibrate(20)
                    VibeType.WORD_FOUND -> v.vibrate(longArrayOf(0, 40, 50, 70), -1)
                    VibeType.INCORRECT -> v.vibrate(120)
                    VibeType.HINT -> v.vibrate(50)
                    VibeType.LEVEL_COMPLETE -> v.vibrate(longArrayOf(0, 60, 50, 60, 50, 140), -1)
                }
            }
        } catch (_: Exception) {
            // Haptic failure should never crash the app
        }
    }
}
