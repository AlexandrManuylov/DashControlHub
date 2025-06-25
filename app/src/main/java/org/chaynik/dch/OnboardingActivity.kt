package org.chaynik.dch

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var cm: ConnectivityManager
    private lateinit var handler: Handler
    private lateinit var callback: ConnectivityManager.NetworkCallback
    private lateinit var timeoutRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            handler = Handler(Looper.getMainLooper())

            val wifiSpecifier = WifiNetworkSpecifier.Builder()
                .setSsid("Chaynik_5G")
                .build()

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiSpecifier)
                .build()

            callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    // Привязываем процесс к нужной сети
                    cm.bindProcessToNetwork(network)

                    // Убираем таймер и cancel поиска
                    handler.removeCallbacks(timeoutRunnable)
                    cm.unregisterNetworkCallback(this)

                    Toast.makeText(this@OnboardingActivity, "Подключено к CB900_WIFI_AP", Toast.LENGTH_SHORT).show()

                    val workRequest = OneTimeWorkRequestBuilder<ConnectWorker>().build()
                    WorkManager.getInstance(this@OnboardingActivity).enqueue(workRequest)

                    finish()
                }
            }

            timeoutRunnable = Runnable {
                // Отменяем поиск — ОЧЕНЬ важно!
                cm.unregisterNetworkCallback(callback)
                Toast.makeText(this, "Не удалось подключиться за 10 сек", Toast.LENGTH_LONG).show()
                finish()
            }

            cm.requestNetwork(networkRequest, callback)
            handler.postDelayed(timeoutRunnable, 10_000)
        } else {
            Toast.makeText(this, "Не поддерживается API < 29", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            cm.unregisterNetworkCallback(callback)
        } catch (e: Exception) {
            // ignore
        }
        handler.removeCallbacks(timeoutRunnable)
    }
}