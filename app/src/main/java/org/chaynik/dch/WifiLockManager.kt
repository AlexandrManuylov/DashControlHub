package org.chaynik.dch

import android.content.Context
import android.net.wifi.WifiManager

class WifiLockManager(private val context: Context) {

    private var wifiLock: WifiManager.WifiLock? = null

    fun acquire() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "DCH_WifiLock")
        wifiLock?.setReferenceCounted(false)
        wifiLock?.acquire()
    }

    fun release() {
        wifiLock?.release()
        wifiLock = null
    }
}