package org.chaynik.dch.presentation.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.chaynik.dch.databinding.FragmentSettingsBinding
import org.chaynik.dch.domain.model.ConnectionState
import androidx.core.net.toUri

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels { SettingsViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConnectWifi.setOnClickListener {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                20
            )
            viewModel.connectToEsp(requireContext())
        }

        val missingPermissions = getMissingPermissions()
        binding.btnRequestPermissions.visibility = if (missingPermissions.isNotEmpty()) View.VISIBLE else View.GONE

        binding.btnRequestPermissions.setOnClickListener {
            val toRequest = getMissingPermissions()
            if (toRequest.isNotEmpty()) {
                requestPermissionsLauncher.launch(toRequest.toTypedArray())
            }
        }

        binding.btnBatteryOptimization.visibility = View.VISIBLE

        binding.btnBatteryOptimization.setOnClickListener {
            val context = requireContext()
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

            if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${requireContext().packageName}".toUri()
                }
                requireActivity().startActivity(intent)
            }
        }

        viewModel.connectionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ConnectionState.Idle -> {}
                is ConnectionState.Connecting -> showToast("Подключение к ESP...")
                is ConnectionState.Success -> showToast("Подключено к ${state.ssid}")
                is ConnectionState.Error -> showToast("Ошибка: ${state.message}")
            }
        }
    }

    private val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun getMissingPermissions(): List<String> {
        return requiredPermissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val denied = result.filterValues { !it }.keys
        if (denied.isEmpty()) {
            Toast.makeText(requireContext(), "Все разрешения выданы", Toast.LENGTH_SHORT).show()
            binding.btnRequestPermissions.visibility = View.GONE
        } else {
            Toast.makeText(requireContext(), "Не выданы: ${denied.joinToString()}", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}