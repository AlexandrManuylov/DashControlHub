package org.chaynik.dch.domain.model.commands

enum class MediaMessageType(val commandMessage: String) {
    NEXT_TRACK("next_track"),
    PREVIEW_TRACK("prev_track"),
    VOLUME_UP("volume_up"),
    VOLUME_DOWN("volume_down"),
    UNKNOWN("");

    companion object {
        @JvmStatic
        fun getCommandMessage(type: String): MediaMessageType = entries.firstOrNull { it.commandMessage == type }
            ?: UNKNOWN
    }
}