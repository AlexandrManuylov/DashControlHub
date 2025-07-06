package org.chaynik.dch.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.chaynik.dch.ConnectWorker
import org.chaynik.dch.ForegroundService

class BootEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.w("ForegroundWork", "onReceive")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.w("ForegroundWork", "onReceive ACTION_BOOT_COMPLETED")
            val serviceIntent = Intent(context, ForegroundService::class.java)
            context.startForegroundService(serviceIntent)

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

        Handler(Looper.getMainLooper()).post {
            WorkManager.getInstance(context).enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.REPLACE,
                work
            )
        }
    }

    private fun log(msg: String) {
        Log.e("NetworkEventReceiver", msg)
    }
}