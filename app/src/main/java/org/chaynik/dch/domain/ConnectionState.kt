package org.chaynik.dch.domain

sealed class ConnectionState {
    object Idle : ConnectionState()
    object Connecting : ConnectionState()
    data class Success(val ssid: String) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}