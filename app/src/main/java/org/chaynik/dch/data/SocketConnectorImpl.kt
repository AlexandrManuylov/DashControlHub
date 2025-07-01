package org.chaynik.dch.data

import android.content.Context
import kotlinx.coroutines.delay
import org.chaynik.dch.WebSocketManager

class SocketConnectorImpl(private val context: Context) : SocketConnector {

    private val webSocketManager by lazy { WebSocketManager(context) }

    override suspend fun connect(): Boolean {
        webSocketManager.connect()
        delay(3000)
        return webSocketManager.isConnected()
    }
}