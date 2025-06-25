package org.chaynik.dch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class ConnectWorker(context: Context, params: WorkerParameters) :  CoroutineWorker(context, params) {

    private lateinit var wifiLockManager: WifiLockManager
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var cm: ConnectivityManager
    private lateinit var callback: ConnectivityManager.NetworkCallback

    override suspend fun doWork(): Result {
        Log.d("ConnectWorker", "Starting ForegroundInfo + WifiLock + WebSocket")

        setForeground(createForegroundInfo())

        wifiLockManager = WifiLockManager(applicationContext)
        wifiLockManager.acquire()

        webSocketManager = WebSocketManager(applicationContext)

        cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        callback = object : ConnectivityManager.NetworkCallback() {

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, capabilities)

                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    val wifiInfo = capabilities.transportInfo as? WifiInfo
                    val ssid = wifiInfo?.ssid?.replace("\"", "")

                    Log.d("ConnectWorker", "onCapabilitiesChanged: ssid = $ssid")

                    if (ssid != "Chaynik_5G") {
                        Log.w("ConnectWorker", "SSID mismatch — cancelling Worker")
                        cancelWork()
                    } else {
                        // Если SSID совпал — можно запускать WebSocket (1 раз)
                        if (!webSocketManager.isConnected()) {
                            webSocketManager.connect()
                        }
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.w("ConnectWorker", "Network LOST — cancelling Worker")
                cancelWork()
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        cm.registerNetworkCallback(networkRequest, callback)

        while (true) {
            delay(10_000)
        }
    }

    private fun cancelWork() {
        try {
            cm.unregisterNetworkCallback(callback)
        } catch (e: Exception) {
            // ignore
        }
        wifiLockManager.release()
        WorkManager.getInstance(applicationContext).cancelWorkById(id)
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notificationId = 2001
        val channelId = "DCH_CONNECT_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                "Dash Control Hub - Connecting",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification: Notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Dash Control Hub")
            .setContentText("Connected to CB900_WIFI_AP")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()

        return ForegroundInfo(notificationId, notification)
    }
}