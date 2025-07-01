package org.chaynik.dch

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.chaynik.dch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge()
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Если в nav_graph для фрагмента задан android:label, он сюда попадёт
            binding.topAppBar.title = destination.label
        }
        // При выборе пункта меню очищаем backStack, чтобы не накапливать историю
        bottomNav.setOnItemSelectedListener { item ->
            val options = androidx.navigation.navOptions {
                // Переход один к одному, без дублей
                launchSingleTop = true
                // Удаляем предыдущие фрагменты из backstack
                popUpTo(navController.graph.startDestinationId) { inclusive = false }
            }
            navController.navigate(item.itemId, null, options)
            true
        }
    }

//    override fun onBackPressed() {
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//        val navController = navHostFragment.navController
//        if (!navController.popBackStack()) {
//            super.onBackPressed()
//        }
//    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // toolbar — отступ сверху
            binding.topAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = sys.top
            }

            // bottom navigation — отступ снизу
            binding.bottomNavigation.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = sys.bottom
            }

            // После «расхода» insets, возвращаем их потребление
            WindowInsetsCompat.CONSUMED
        }
    }
}

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        if (isNotificationServiceEnabled()) {
//            Log.d("DCH", "Notification service is enabled")
//            restartNotificationService()
//        } else {
//            Log.w("DCH", "Notification service NOT enabled")
//            restartNotificationService()
//            // Тут можно показать экран "пожалуйста включите доступ к уведомлениям"
//        }
//        viewModel.connectionStatus.observe(this) { status ->
//            binding.textStatus.text = status
//        }
//
//        binding.btnOnboarding.setOnClickListener {
//            startActivity(Intent(this, OnboardingActivity::class.java))
//        }
//
//        binding.btnStartService.setOnClickListener {
//            startForegroundService(Intent(this, ForegroundService::class.java))
//        }
//
//    }
//    fun restartNotificationService() {
//        val pm = packageManager
//        val componentName = ComponentName(this, DchNotificationListenerService::class.java)
//
//        pm.setComponentEnabledSetting(
//            componentName,
//            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//            android.content.pm.PackageManager.DONT_KILL_APP
//        )
//
//        pm.setComponentEnabledSetting(
//            componentName,
//            android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            android.content.pm.PackageManager.DONT_KILL_APP
//        )
//
//        Log.d("DCH", "NotificationListenerService restarted")
//    }
//
//    fun isNotificationServiceEnabled(): Boolean {
//        val cn = ComponentName(this, DchNotificationListenerService::class.java)
//        val flat = android.provider.Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
//        return flat != null && flat.contains(cn.flattenToString())
//    }
//}