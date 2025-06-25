package org.chaynik.dch

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.util.Log

class MediaCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action")
        if (action == null) {
            Log.w("DCH", "MediaCommandReceiver: no action")
            return
        }

        Log.d("DCH", "MediaCommandReceiver got action: $action")

        // получаем все MediaSession, связанные с нашей NotificationListenerService
        val mgr = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val sessions = mgr.getActiveSessions(
            ComponentName(context, DchNotificationListenerService::class.java)
        )

        sessions.forEach { ctrl ->
            when (action) {
                "next" -> {
                    Log.d("DCH", "skipToNext on ${ctrl.packageName}")
                    ctrl.transportControls.skipToNext()
                }
                "prev" -> {
                    Log.d("DCH", "skipToPrev on ${ctrl.packageName}")
                    ctrl.transportControls.skipToPrevious()
                }
                "play" -> {
                    Log.d("DCH", "play on ${ctrl.packageName}")
                    ctrl.transportControls.play()
                }
                "pause" -> {
                    Log.d("DCH", "pause on ${ctrl.packageName}")
                    ctrl.transportControls.pause()
                }
                // ... можно добавить volume_up/down здесь, если нужно
                else -> Log.w("DCH", "Unknown media command: $action")
            }
        }
    }
}