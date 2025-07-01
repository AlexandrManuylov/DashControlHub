package org.chaynik.dch.data

interface WifiRepository {
    fun getCurrentSsid(): String?
    fun saveSsid(ssid: String)
    fun getSavedSsid(): String?
}