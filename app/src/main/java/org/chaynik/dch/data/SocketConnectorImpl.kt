package org.chaynik.dch.data

import android.content.Context
import kotlinx.coroutines.delay
import org.chaynik.dch.domain.usecase.HandleCommandUseCase
import org.chaynik.dch.domain.usecase.HandleCommandUseCaseImpl

class SocketConnectorImpl(private val context: Context) : SocketConnector {


    private val webSocketRepository: WebSocketManager by lazy {
        val commandUseCase: HandleCommandUseCase = HandleCommandUseCaseImpl()
        WebSocketManagerImpl(commandUseCase)
    }

    override suspend fun connect(): Boolean {
        webSocketRepository.connect()
        delay(3000)
        return webSocketRepository.isConnected()
    }
}