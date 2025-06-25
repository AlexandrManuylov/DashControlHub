package org.chaynik.dch

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.SystemClock
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.KeyEvent
import android.content.IntentFilter

class DchNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("DCH", "NotificationListenerService connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // Можно не обрабатывать пока — используем только для доступа к MediaSessionManager
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Тоже пока ничего не делаем
    }
    private fun skipToNext() {
        val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = mediaSessionManager.getActiveSessions(ComponentName(this, DchNotificationListenerService::class.java))
        controllers.forEach { controller ->
            Log.d("DCH", "skipToNext on ${controller.packageName}")
            controller.transportControls.skipToNext()
        }
    }

    private fun skipToPrev() {
        val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = mediaSessionManager.getActiveSessions(ComponentName(this, DchNotificationListenerService::class.java))
        controllers.forEach { controller ->
            Log.d("DCH", "skipToPrev on ${controller.packageName}")
            controller.transportControls.skipToPrevious()
        }
    }

    private fun pause() {
        val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = mediaSessionManager.getActiveSessions(ComponentName(this, DchNotificationListenerService::class.java))
        controllers.forEach { controller ->
            Log.d("DCH", "pause on ${controller.packageName}")
            controller.transportControls.pause()
        }
    }

    private fun play() {
        val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = mediaSessionManager.getActiveSessions(ComponentName(this, DchNotificationListenerService::class.java))
        controllers.forEach { controller ->
            Log.d("DCH", "play on ${controller.packageName}")
            controller.transportControls.play()
        }
    }

    fun sendMediaCommand(keyCode: Int) {
        val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = mediaSessionManager.getActiveSessions(ComponentName(this, DchNotificationListenerService::class.java))

        controllers.forEach { controller ->
            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_NEXT -> controller.transportControls.skipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> controller.transportControls.skipToPrevious()
                else -> {
                    // fallback на media button
                    val eventTime = SystemClock.uptimeMillis()
                    val downEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0)
                    val upEvent = KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0)

                    controller.dispatchMediaButtonEvent(downEvent)
                    controller.dispatchMediaButtonEvent(upEvent)
                }
            }
        }
    }
}