package com.ttws.wordhunterquest.managers

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(private val progressManager: ProgressManager) {

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    enum class SoundType {
        BUTTON_CLICK,
        CORRECT_WORD,
        INCORRECT_SELECTION,
        HINT,
        LEVEL_COMPLETED,
        LEVEL_UNLOCKED
    }

    fun playSound(type: SoundType) {
        if (!progressManager.isSoundEnabled()) return

        scope.launch {
            try {
                val pcmData = when (type) {
                    SoundType.BUTTON_CLICK -> generateTone(800.0, 40)
                    SoundType.CORRECT_WORD -> generateChime(listOf(523.25, 659.25, 783.99, 1046.50), 70)
                    SoundType.INCORRECT_SELECTION -> generateTone(160.0, 140, isSquare = true)
                    SoundType.HINT -> generateChime(listOf(880.0, 1174.66, 1396.91), 85)
                    SoundType.LEVEL_COMPLETED -> generateFanfare()
                    SoundType.LEVEL_UNLOCKED -> generateChime(listOf(587.33, 739.99, 880.0, 1174.66), 80)
                }
                playPcm(pcmData)
            } catch (_: Exception) {
                // Audio failure should never crash the game
            }
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, isSquare: Boolean = false): ShortArray {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val envelope = 1.0 - (i.toDouble() / numSamples) // Fade out
            val wave = if (isSquare) {
                if (sin(2.0 * Math.PI * frequency * time) >= 0) 0.6 else -0.6
            } else {
                sin(2.0 * Math.PI * frequency * time)
            }
            samples[i] = (wave * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
        }
        return samples
    }

    private fun generateChime(frequencies: List<Double>, noteDurationMs: Int): ShortArray {
        val noteSamples = (sampleRate * (noteDurationMs / 1000.0)).toInt()
        val totalSamples = noteSamples * frequencies.size
        val samples = ShortArray(totalSamples)

        frequencies.forEachIndexed { noteIdx, freq ->
            val startSample = noteIdx * noteSamples
            for (i in 0 until noteSamples) {
                val time = i.toDouble() / sampleRate
                val envelope = 1.0 - (i.toDouble() / noteSamples)
                val wave = sin(2.0 * Math.PI * freq * time)
                samples[startSample + i] = (wave * envelope * Short.MAX_VALUE * 0.5).toInt().toShort()
            }
        }
        return samples
    }

    private fun generateFanfare(): ShortArray {
        val notes = listOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
        val noteDurations = listOf(90, 90, 90, 140, 260)
        val totalSamples = noteDurations.sumOf { (sampleRate * (it / 1000.0)).toInt() }
        val samples = ShortArray(totalSamples)

        var currentSample = 0
        for (idx in notes.indices) {
            val freq = notes[idx]
            val durationMs = noteDurations[idx]
            val count = (sampleRate * (durationMs / 1000.0)).toInt()
            for (i in 0 until count) {
                val time = i.toDouble() / sampleRate
                val envelope = 1.0 - (i.toDouble() / count * 0.7)
                val wave = sin(2.0 * Math.PI * freq * time)
                if (currentSample + i < totalSamples) {
                    samples[currentSample + i] = (wave * envelope * Short.MAX_VALUE * 0.55).toInt().toShort()
                }
            }
            currentSample += count
        }
        return samples
    }

    private fun playPcm(pcmData: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(pcmData.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(pcmData, 0, pcmData.size)
        audioTrack.play()
        // Release after playback finishes
        scope.launch {
            try {
                val durationMs = (pcmData.size.toDouble() / sampleRate * 1000).toLong() + 50
                kotlinx.coroutines.delay(durationMs)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
