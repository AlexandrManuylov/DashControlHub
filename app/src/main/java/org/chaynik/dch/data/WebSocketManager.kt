package org.chaynik.dch.data

interface WebSocketManager {
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
}