package de.pacheco.recorder.waverecorder

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import java.io.Closeable

object Sounds : Closeable {

    private var soundPool: SoundPool? = createSoundPool()
    private val sounds: MutableMap<String, Int> = HashMap()

    private fun createSoundPool(): SoundPool {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            @Suppress("DEPRECATION")
            SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        }
    }

    private fun release() {
        sounds.values.forEach { soundPool?.unload(it) }
        sounds.clear()
        soundPool?.release()
        soundPool = null
    }

    fun play(filePath: String) {
        soundPool = soundPool ?: createSoundPool()
        soundPool?.play(sounds[filePath] ?: 0, 1f, 1f, 0, 0, 1f)
    }

    fun load(filePath: String) {
        soundPool = soundPool ?: createSoundPool()
        sounds += (filePath to (soundPool?.load(filePath, 1) ?: 0))
    }

    override fun close() {
        release()
    }
}