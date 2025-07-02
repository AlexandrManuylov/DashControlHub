package org.chaynik.dch.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.chaynik.dch.ConnectWorker

class NetworkEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                log("BOOT_COMPLETED — запускаем ConnectWorker")
                scheduleConnectWorker(context, "connect_worker_on_boot")
            }

            WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                if (isWifiConnected(context)) {
                    scheduleConnectWorker(context, "connect_worker_wifi")
                }
            }
        }
    }

    private fun isWifiConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun scheduleConnectWorker(context: Context, workName: String) {
        val work = OneTimeWorkRequestBuilder<ConnectWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    private fun log(msg: String) {
        Log.d("NetworkEventReceiver", msg)
    }
}