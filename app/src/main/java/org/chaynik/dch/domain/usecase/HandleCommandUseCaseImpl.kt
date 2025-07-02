package org.chaynik.dch.domain.usecase

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import org.chaynik.dch.MediaCommandReceiver
import org.json.JSONObject

class HandleCommandUseCaseImpl(
    private val appContext: Context
) : HandleCommandUseCase {

    override fun execute(command: String) {
        try {
            val cmd = JSONObject(command).getString("cmd")
            when (cmd) {
                "volume_up" -> adjustVolume(AudioManager.ADJUST_RAISE)
                "volume_down" -> adjustVolume(AudioManager.ADJUST_LOWER)
                "next_track" -> sendMediaIntent("next")
                "prev_track" -> sendMediaIntent("prev")
                // Добавьте другие команды
            }
        } catch (e: Exception) {
            Log.e("HandleCommand", "Invalid JSON: $command", e)
        }
    }

    private fun sendMediaIntent(action: String) {
        val intent = Intent(appContext, MediaCommandReceiver::class.java).apply {
            putExtra("action", action)
        }
        appContext.sendBroadcast(intent)
    }

    private fun adjustVolume(direction: Int) {
        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction,
            AudioManager.FLAG_SHOW_UI
        )
    }
}