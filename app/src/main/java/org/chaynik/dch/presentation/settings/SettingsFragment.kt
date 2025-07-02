package org.chaynik.dch.presentation.settings

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.chaynik.dch.databinding.FragmentSettingsBinding
import org.chaynik.dch.domain.ConnectionState

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

        viewModel.connectionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ConnectionState.Idle -> {}
                is ConnectionState.Connecting -> showToast("Подключение к ESP...")
                is ConnectionState.Success -> showToast("Подключено к ${state.ssid}")
                is ConnectionState.Error -> showToast("Ошибка: ${state.message}")
            }
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