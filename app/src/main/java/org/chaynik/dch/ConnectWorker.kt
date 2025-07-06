package org.chaynik.dch

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ConnectWorker(val context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!App.getInstance().getWebSocketManager().isConnected()) {
                Log.w("ForegroundWork", "Start connecting")
                App.getInstance().getWebSocketManager().connect()
                delay(3000)
            }

            if (App.getInstance().getWebSocketManager().isConnected()) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

}