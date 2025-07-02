package org.chaynik.dch.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.chaynik.dch.domain.usecase.HandleCommandUseCase

class WebSocketManagerImpl(
    private val handleCommandUseCase: HandleCommandUseCase,
    appContext: Context
) : WebSocketRepository {

    private val appContext = appContext.applicationContext
    private var webSocket: WebSocket? = null
    private var connected = false
    private var connecting = false

    private val serverUrl = "ws://192.168.1.63:12346"

    override fun connect() {
        if (connected || connecting) return
        connecting = true

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder().url(serverUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                connected = true
                connecting = false
                ws.send("Hello from Dash Control Hub!")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                handleCommandUseCase.execute(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                connected = false
                connecting = false
                reconnect()
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                connected = false
                connecting = false
            }
        })
    }

    override fun disconnect() {
        connected = false
        connecting = false
        webSocket?.close(1000, "Normal closure")
    }

    override fun isConnected(): Boolean = connected

    private fun reconnect() {
        Handler(Looper.getMainLooper()).postDelayed({ connect() }, 5000)
    }
}