package org.chaynik.dch

object DchMediaController {
    var notificationListener: DchNotificationListenerService? = null

    fun sendMediaCommand(keyCode: Int) {
        notificationListener?.sendMediaCommand(keyCode)
    }
}