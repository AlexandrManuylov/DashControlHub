package org.chaynik.dch.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.chaynik.dch.data.SocketConnectorImpl
import org.chaynik.dch.data.WebSocketManagerImpl
import org.chaynik.dch.data.WebSocketRepository
import org.chaynik.dch.data.WifiRepositoryImpl
import org.chaynik.dch.domain.usecase.CheckWifiConnectionUseCase
import org.chaynik.dch.domain.usecase.ConnectAndSaveSsidUseCase
import org.chaynik.dch.domain.usecase.HandleCommandUseCaseImpl

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val wifiRepo = WifiRepositoryImpl(context)
        val socketConnector = SocketConnectorImpl(context)
        val commandUseCase = HandleCommandUseCaseImpl(context)
        val webSocketManager: WebSocketRepository = WebSocketManagerImpl(commandUseCase, context)
        return SettingsViewModel(
            ConnectAndSaveSsidUseCase(wifiRepo, socketConnector),
            CheckWifiConnectionUseCase(wifiRepo),
            commandUseCase,
            webSocketManager
        ) as T
    }
}