package org.chaynik.dch.domain.usecase

import org.chaynik.dch.data.WifiRepository

class CheckWifiConnectionUseCase(private val wifiRepository: WifiRepository) {
    fun execute(): Pair<String?, String?> {
        return wifiRepository.getSavedSsid() to wifiRepository.getCurrentSsid()
    }
}