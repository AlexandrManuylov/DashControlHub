package org.chaynik.dch

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class WebSocketManager(private val context: Context) {

    private var webSocket: WebSocket? = null
    private var connected = false
    private var connecting = false

    private val serverUrl = "ws://192.168.1.63:12346"  // Поставь сюда актуальный IP твоего ПК

    fun connect() {
        if (connected) {
            Log.d("WebSocket", "Already connected — skipping connect")
            return
        }
        if (connecting) {
            Log.d("WebSocket", "Already connecting — skipping connect")
            return
        }

        connecting = true
        Log.d("WebSocket", "Trying to connect to $serverUrl")

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS) // infinite read timeout for WebSocket
            .build()

        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                connected = true
                connecting = false
                Log.d("WebSocket", "Connected to $serverUrl")
                ws.send("Hello from Dash Control Hub!")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                Log.d("WebSocket", "Message: $text")
                handleIncomingCommand(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                connected = false
                connecting = false
                Log.e("WebSocket", "Failure: ${t.localizedMessage ?: "unknown error"}")
                Log.d("WebSocket", "Reconnecting in 5 seconds...")
                reconnect()
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                connected = false
                connecting = false
                Log.d("WebSocket", "Closed: $reason")
            }
        })
    }

    private fun handleIncomingCommand(json: String) {
        try {
            val obj = JSONObject(json)
            val cmd = obj.getString("cmd")
            when (cmd) {
                "volume_up" -> {
                    Log.d("DCH", "CMD: VOLUME UP")
                    adjustVolume(AudioManager.ADJUST_RAISE)
                }
                "volume_down" -> {
                    Log.d("DCH", "CMD: VOLUME DOWN")
                    adjustVolume(AudioManager.ADJUST_LOWER)
                }
                "next_track" -> {
                    Log.d("DCH", "CMD: NEXT TRACK")
                    sendMediaCommandAction("next")
                }
                "prev_track" -> {
                    Log.d("DCH", "CMD: PREV TRACK")
                    sendMediaCommandAction("prev")
                }
                "accept_call" -> {
                    Log.d("DCH", "CMD: ACCEPT CALL")
                    // пока заглушка
                }
                "reject_call" -> {
                    Log.d("DCH", "CMD: REJECT CALL")
                    // пока заглушка
                }
                else -> Log.w("DCH", "Unknown CMD: $cmd")
            }
        } catch (e: Exception) {
            Log.e("DCH", "Invalid command JSON", e)
        }
    }

    private fun sendMediaCommandAction(action: String) {
        val intent = Intent(context, MediaCommandReceiver::class.java).apply {
            putExtra("action", action)
        }
        context.sendBroadcast(intent)
    }

    private fun adjustVolume(direction: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            direction,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun disconnect() {
        connected = false
        connecting = false
        webSocket?.close(1000, "Normal closure")
        Log.d("WebSocket", "Manually disconnected")
    }

    fun isConnected(): Boolean = connected

    private fun reconnect() {
        Handler(Looper.getMainLooper()).postDelayed({
            connect()
        }, 5000)
    }
}