package org.chaynik.dch.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.chaynik.dch.domain.usecase.CheckWifiConnectionUseCase
import org.chaynik.dch.domain.usecase.ConnectAndSaveSsidUseCase

class SettingsViewModel(
    private val connectAndSaveSsidUseCase: ConnectAndSaveSsidUseCase,
    private val checkWifiConnectionUseCase: CheckWifiConnectionUseCase
) : ViewModel() {

    private val _statusText = MutableLiveData<String>()
    val statusText: LiveData<String> = _statusText

    fun connectToEsp(onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = connectAndSaveSsidUseCase.execute()
            if (result != null) {
                _statusText.value = "Monitoring $result"
                onSuccess(result)
            } else {
                _statusText.value = "Failed to connect"
                onFailure()
            }
        }
    }

    fun checkConnectionStatus() {
        val (saved, current) = checkWifiConnectionUseCase.execute()
        if (saved != null && saved == current) {
            _statusText.value = "Connected to $saved"
        } else {
            _statusText.value = "SSID not matched or not saved"
        }
    }
}