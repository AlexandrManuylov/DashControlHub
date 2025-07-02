package org.chaynik.dch

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager

object WifiHelper {

    fun isWifiEnabled(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    fun getConnectedSsid(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        val ssid = info?.ssid ?: return null

        return if (ssid == "<unknown ssid>") null else ssid.removeSurrounding("\"")
    }

    fun saveSsidToPrefs(context: Context, ssid: String) {
        val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("esp_ssid", ssid).apply()
    }
}