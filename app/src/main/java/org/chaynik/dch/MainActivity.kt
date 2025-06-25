package org.chaynik.dch

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.chaynik.dch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (isNotificationServiceEnabled()) {
            Log.d("DCH", "Notification service is enabled")
            restartNotificationService()
        } else {
            Log.w("DCH", "Notification service NOT enabled")
            restartNotificationService()
            // Тут можно показать экран "пожалуйста включите доступ к уведомлениям"
        }
        viewModel.connectionStatus.observe(this) { status ->
            binding.textStatus.text = status
        }

        binding.btnOnboarding.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }

        binding.btnStartService.setOnClickListener {
            startForegroundService(Intent(this, ForegroundService::class.java))
        }

    }
    fun restartNotificationService() {
        val pm = packageManager
        val componentName = ComponentName(this, DchNotificationListenerService::class.java)

        pm.setComponentEnabledSetting(
            componentName,
            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            android.content.pm.PackageManager.DONT_KILL_APP
        )

        pm.setComponentEnabledSetting(
            componentName,
            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            android.content.pm.PackageManager.DONT_KILL_APP
        )

        Log.d("DCH", "NotificationListenerService restarted")
    }

    fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, DchNotificationListenerService::class.java)
        val flat = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat != null && flat.contains(cn.flattenToString())
    }
}