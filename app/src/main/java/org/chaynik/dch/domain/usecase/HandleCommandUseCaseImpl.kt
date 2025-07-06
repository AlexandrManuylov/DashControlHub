package org.chaynik.dch.domain.usecase

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import org.chaynik.dch.App
import org.chaynik.dch.MediaCommandReceiver
import org.chaynik.dch.domain.model.commands.Commands
import org.chaynik.dch.domain.model.commands.MediaMessageType
import org.json.JSONObject

class HandleCommandUseCaseImpl() : HandleCommandUseCase {

    override fun execute(command: Commands) {
        when (command) {
            is Commands.CommandsMedia -> executeMediaCommand(command)
            is Commands.CommandsSTM32 -> {}
            is Commands.Unknown -> {}
        }
    }

    private fun executeMediaCommand(media: Commands.CommandsMedia) {
        when(media.message){
            MediaMessageType.NEXT_TRACK,MediaMessageType.PREVIEW_TRACK -> {
                sendMediaIntent(media.message.commandMessage)
            }
            MediaMessageType.VOLUME_UP -> adjustVolume(AudioManager.ADJUST_RAISE)
            MediaMessageType.VOLUME_DOWN -> adjustVolume(AudioManager.ADJUST_LOWER)
            MediaMessageType.UNKNOWN -> {}
        }
    }

    private fun sendMediaIntent(action: String) {
        val intent = Intent(App.getInstance(), MediaCommandReceiver::class.java).apply {
            putExtra("action", action)
        }
        App.getInstance().sendBroadcast(intent)
    }

    private fun adjustVolume(direction: Int) {
        val audioManager = App.getInstance().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction,
            AudioManager.FLAG_SHOW_UI
        )
    }

}