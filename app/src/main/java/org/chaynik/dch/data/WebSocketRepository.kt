package org.chaynik.dch.data

interface WebSocketRepository {
    fun connect()
    fun disconnect()
    fun isConnected(): Boolean
}