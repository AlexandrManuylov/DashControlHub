package org.chaynik.dch.data

interface SocketConnector {
    suspend fun connect(): Boolean
}