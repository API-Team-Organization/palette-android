package com.api.palette.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.api.palette.R
import com.api.palette.databinding.FragmentSettingBinding
import com.api.palette.ui.util.changeFragment

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        loadUserNameInfo()
        binding.llLogout.setOnClickListener { showLogoutDialog() }
        binding.llResign.setOnClickListener { resignDialog(requireContext()) }
        binding.llPrivacyPolicy.setOnClickListener { goToPrivacyPolicyPage() }
        binding.llAppInfo.setOnClickListener { goToAppInfoPage() }
        binding.llMy.setOnClickListener { changeFragment(MyInfoFragment()) }
        return binding.root
    }

    private fun loadUserNameInfo() {
        val prefs = com.api.palette.application.PaletteApplication.prefs
        binding.tvUserName.text = prefs.username
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        val noLogoutTextView = dialogView.findViewById<android.widget.TextView>(R.id.noLogoutTextView)
        val logoutTextView = dialogView.findViewById<android.widget.TextView>(R.id.logoutTextView)
        noLogoutTextView.setOnClickListener { dialog.dismiss() }
        logoutTextView.setOnClickListener {
            logout()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun logout() {
        com.api.palette.application.PaletteApplication.prefs.clearToken()
        com.api.palette.application.PaletteApplication.prefs.clearUser()
        val intent = android.content.Intent(activity, com.api.palette.MainActivity::class.java)
        requireActivity().startActivity(intent)
        activity?.finish()
    }

    private fun goToPrivacyPolicyPage() {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://dgsw-team-api.notion.site/cc32c87f614e4798893293abfe5ca72a"))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToAppInfoPage() {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"))
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun resignDialog(context: android.content.Context) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_resign, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        val noTextView = dialogView.findViewById<android.widget.TextView>(R.id.noTextView)
        val yesTextView = dialogView.findViewById<android.widget.TextView>(R.id.yesTextView)
        noTextView.setOnClickListener { dialog.dismiss() }
        yesTextView.setOnClickListener {
            resign()
            val intent = android.content.Intent(context, com.api.palette.MainActivity::class.java)
            context.startActivity(intent)
            (context as? android.app.Activity)?.finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun resign() {

    }
}
