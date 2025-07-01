package org.chaynik.dch.data

import android.content.Context
import android.net.wifi.WifiManager

class WifiRepositoryImpl(private val context: Context) : WifiRepository {

    private val prefs = context.getSharedPreferences("wifi_prefs", Context.MODE_PRIVATE)

    override fun getCurrentSsid(): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.connectionInfo?.ssid?.removePrefix("\"")?.removeSuffix("\"")
    }

    override fun saveSsid(ssid: String) {
        prefs.edit().putString("saved_ssid", ssid).apply()
    }

    override fun getSavedSsid(): String? {
        return prefs.getString("saved_ssid", null)
    }
}