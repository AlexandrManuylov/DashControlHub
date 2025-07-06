package org.chaynik.dch.domain.model.commands

sealed class Commands() {
    class CommandsMedia(
        val message: MediaMessageType
    ) : Commands()

    class CommandsSTM32(
        val message: String? = null
    ) : Commands()

    class Unknown(
        val message: String? = null
    ) : Commands()
}