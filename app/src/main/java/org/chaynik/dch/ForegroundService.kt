package org.chaynik.dch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    private lateinit var wifiLockManager: WifiLockManager
    private lateinit var webSocketManager: WebSocketManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        wifiLockManager = WifiLockManager(this)
        wifiLockManager.acquire()

        webSocketManager = WebSocketManager(this)
        webSocketManager.connect()

        startForeground(1001, buildNotification("Dash Control Hub connected"))
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
        wifiLockManager.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "DCH_CHANNEL")
            .setContentTitle("Dash Control Hub")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "DCH_CHANNEL", "Dash Control Hub Service", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}