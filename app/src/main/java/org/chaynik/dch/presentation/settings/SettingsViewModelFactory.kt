package org.chaynik.dch.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.chaynik.dch.data.SocketConnectorImpl
import org.chaynik.dch.data.WifiRepositoryImpl
import org.chaynik.dch.domain.usecase.CheckWifiConnectionUseCase
import org.chaynik.dch.domain.usecase.ConnectAndSaveSsidUseCase

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val wifiRepo = WifiRepositoryImpl(context)
        val socketConnector = SocketConnectorImpl(context)
        return SettingsViewModel(
            ConnectAndSaveSsidUseCase(wifiRepo, socketConnector),
            CheckWifiConnectionUseCase(wifiRepo)
        ) as T
    }
}