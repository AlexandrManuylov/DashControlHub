package org.chaynik.dch.presentation.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.chaynik.dch.WifiHelper
import org.chaynik.dch.data.SocketConnector
import org.chaynik.dch.data.WebSocketManager
import org.chaynik.dch.domain.model.ConnectionState
import org.chaynik.dch.domain.usecase.CheckWifiConnectionUseCase
import org.chaynik.dch.domain.usecase.HandleCommandUseCase

class SettingsViewModel(
    private val socketConnector: SocketConnector,
    private val checkWifiConnectionUseCase: CheckWifiConnectionUseCase,
    private val handleCommandUseCase: HandleCommandUseCase,
    private val webSocketRepository: WebSocketManager,
) : ViewModel() {

    private val _connectionState = MutableLiveData<ConnectionState>(ConnectionState.Idle)
    val connectionState: LiveData<ConnectionState> = _connectionState

    fun connectToEsp(context: Context) {
        viewModelScope.launch {
            if (!WifiHelper.isWifiEnabled(context)) {
                _connectionState.value = ConnectionState.Error("Wi-Fi отключён")
                return@launch
            }

            val ssid = WifiHelper.getConnectedSsid(context)
            if (ssid == null) {
                _connectionState.value = ConnectionState.Error("Нет подключения к Wi-Fi")
                return@launch
            }

            _connectionState.value = ConnectionState.Connecting

            val connected = socketConnector.connect()
            if (connected) {
                WifiHelper.saveSsidToPrefs(context, ssid)
                _connectionState.value = ConnectionState.Success(ssid)
            } else {
                _connectionState.value = ConnectionState.Error("Ошибка подключения к серверу")
            }
        }
    }

}