package org.chaynik.dch.domain.usecase

import android.content.Context
import kotlinx.coroutines.delay
import org.chaynik.dch.WebSocketManager
import org.chaynik.dch.data.WifiRepository

class ConnectAndSaveSsidUseCase(  private val wifiRepository: WifiRepository,
                                  private val socketConnector: SocketConnector
) {
    suspend fun execute(): String? {
        val ssid = wifiRepository.getCurrentSsid()
        if (ssid.isNullOrBlank() || ssid == "<unknown ssid>") return null

        val connected = socketConnector.connect()
        if (connected) {
            wifiRepository.saveSsid(ssid)
            return ssid
        }

        return null
    }
}