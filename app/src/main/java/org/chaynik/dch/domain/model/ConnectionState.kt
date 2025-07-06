package org.chaynik.dch.domain.model

sealed class ConnectionState {
    object Idle : ConnectionState()
    object Connecting : ConnectionState()
    data class Success(val ssid: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}