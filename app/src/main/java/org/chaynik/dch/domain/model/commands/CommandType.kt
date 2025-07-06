package org.chaynik.dch.domain.model.commands

enum class CommandType(val type: String) {
    MEDIA("media"),
    STM32("stm32"),
    UNKNOWN("");

    companion object {
        @JvmStatic
        fun getType(type: String): CommandType = entries.firstOrNull { it.type == type }
            ?: UNKNOWN
    }
}