package org.chaynik.dch

import android.app.Application
import org.chaynik.dch.data.WebSocketManagerImpl
import org.chaynik.dch.data.WebSocketManager
import org.chaynik.dch.domain.usecase.HandleCommandUseCaseImpl

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        private lateinit var instance: App

        private val webSocketManager: WebSocketManager by lazy {
            WebSocketManagerImpl(
                HandleCommandUseCaseImpl()
            )
        }

        fun getInstance(): App {
            return instance
        }

    }

    fun getWebSocketManager(): WebSocketManager {
        return webSocketManager
    }
}