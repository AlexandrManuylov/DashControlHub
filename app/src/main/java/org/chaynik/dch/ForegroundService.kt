package org.chaynik.dch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "wifi_monitor_channel"
        private const val TARGET_SSID = "Chaynik_5G"
        private const val SERVICE_ID = 5222
        private const val MSG_WAITING_CONNECTION = "Подключитесь к WiFi"
        private const val MSG_CONNECTED = "Подключено через"
        private const val MSG_NEED_FIRST_CONNECTING = "Необходимо настроить подключение"
    }

    private lateinit var wifiLockManager: WifiLockManager
    private lateinit var notificationManager: NotificationManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onUnavailable() {
            super.onUnavailable()
            Log.w("ForegroundWork", "onUnavailable")

        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            Log.w("ForegroundWork", "onLosing")

        }

        override fun onLost(network: Network) {
            super.onLost(network)
            updateNotification("Ожидание подключения к $TARGET_SSID")

        }

        override fun onAvailable(network: Network) {
            Log.w("ForegroundWork", "onAvailable")

            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(network)
            if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                Log.w("ForegroundWork", "TRANSPORT_WIFI")

                val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                val ssid = wifiManager.connectionInfo.ssid.removePrefix("\"").removeSuffix("\"")
                if (ssid == TARGET_SSID) {
                    updateNotification("$MSG_CONNECTED $TARGET_SSID")
                    val workRequest = OneTimeWorkRequestBuilder<ConnectWorker>().build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                } else {
                    updateNotification("$MSG_WAITING_CONNECTION $TARGET_SSID")
                }
            } else {
                Log.w("ForegroundWork", "UNKNOWN NetworkCapabilities")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.w("ForegroundWork", "onCreate")
        wifiLockManager = WifiLockManager(applicationContext)
        wifiLockManager.acquire()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        cm.registerNetworkCallback(request, networkCallback)
        monitorConnection()
    }

    override fun onDestroy() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, buildNotification(MSG_WAITING_CONNECTION))
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "DCH Foreground Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Dash Control Hub")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(content: String) {
        val updated = buildNotification(content)
        notificationManager.notify(SERVICE_ID, updated)
    }

    private fun monitorConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val ssid = WifiHelper.getConnectedSsid(this@ForegroundService)
                if (ssid == TARGET_SSID) {
                    updateNotification("$MSG_CONNECTED $TARGET_SSID")
                } else {
                    updateNotification("$MSG_WAITING_CONNECTION $TARGET_SSID")
                }
                delay(5000)
            }
        }
    }
}

