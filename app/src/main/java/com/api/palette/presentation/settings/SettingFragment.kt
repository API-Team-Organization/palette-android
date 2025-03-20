package com.api.palette.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentSettingBinding
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        loadUserNameInfo()
        binding.llLogout.setOnClickListener { showLogoutDialog() }
        binding.llResign.setOnClickListener { showResignDialog() }
        binding.llPrivacyPolicy.setOnClickListener { goToPrivacyPolicyPage() }
        binding.llAppInfo.setOnClickListener { goToAppInfoPage() }
        binding.llMy.setOnClickListener { findNavController().navigate(R.id.action_settingFragment_to_myInfoFragment) }
        return binding.root
    }

    private fun loadUserNameInfo() {
        binding.tvUserName.text = PaletteApplication.prefs.username
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).setCancelable(false).create()
        val noLogoutTextView = dialogView.findViewById<TextView>(R.id.noLogoutTextView)
        val logoutTextView = dialogView.findViewById<TextView>(R.id.logoutTextView)
        noLogoutTextView.setOnClickListener { dialog.dismiss() }
        logoutTextView.setOnClickListener {
            PaletteApplication.prefs.clearToken()
            PaletteApplication.prefs.clearUser()
            findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showResignDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_resign, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).setCancelable(false).create()
        val noTextView = dialogView.findViewById<TextView>(R.id.noTextView)
        val yesTextView = dialogView.findViewById<TextView>(R.id.yesTextView)
        noTextView.setOnClickListener { dialog.dismiss() }
        yesTextView.setOnClickListener {
            PaletteApplication.prefs.clearToken()
            PaletteApplication.prefs.clearUser()
            findNavController().navigate(R.id.action_settingFragment_to_loginFragment)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun goToPrivacyPolicyPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dgsw-team-api.notion.site/cc32c87f614e4798893293abfe5ca72a"))
        startActivity(intent)
    }

    private fun goToAppInfoPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
