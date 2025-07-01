package org.chaynik.dch.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import org.chaynik.dch.databinding.FragmentSettingsBinding

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
            viewModel.saveCurrentSsid(
                onSuccess = { ssid ->
                    Snackbar.make(binding.root, "Saved SSID: $ssid", Snackbar.LENGTH_SHORT).show()
                },
                onFailure = {
                    Snackbar.make(binding.root, "SSID not found", Snackbar.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.statusText.observe(viewLifecycleOwner) {
            binding.tvWifiStatus.text = "Status: $it"
        }

        viewModel.checkConnectionStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}