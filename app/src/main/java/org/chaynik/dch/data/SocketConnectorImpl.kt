package org.chaynik.dch.data

import org.chaynik.dch.WebSocketManager

class SocketConnectorImpl : SocketConnector {
    override suspend fun connect(): Boolean {
        WebSocketManager.connect()
        delay(3000)
        return WebSocketManager.isConnected()
    }
}