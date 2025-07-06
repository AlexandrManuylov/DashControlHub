package org.chaynik.dch.data.model

import com.google.gson.annotations.SerializedName
import org.chaynik.dch.domain.model.commands.CommandType
import org.chaynik.dch.domain.model.commands.Commands
import org.chaynik.dch.domain.model.commands.Commands.CommandsMedia
import org.chaynik.dch.domain.model.commands.Commands.CommandsSTM32
import org.chaynik.dch.domain.model.commands.Commands.Unknown
import org.chaynik.dch.domain.model.commands.MediaMessageType

class CommandsData(
    @SerializedName("commandType") val commandType: String,
    @SerializedName("message") val message: String
)

fun CommandsData.convertToDomain(): Commands {
    return when (CommandType.getType(commandType)) {
        CommandType.MEDIA -> CommandsMedia(MediaMessageType.getCommandMessage(message))
        CommandType.STM32 -> CommandsSTM32(message = message)
        CommandType.UNKNOWN -> Unknown(message = message)

    }
}