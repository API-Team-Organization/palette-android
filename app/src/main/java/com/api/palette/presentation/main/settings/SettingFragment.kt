package com.api.palette.presentation.main.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.api.palette.MainActivity
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentSettingBinding
import com.api.palette.presentation.main.settings.info.MyInfoFragment
import com.api.palette.presentation.main.settings.viewmodel.SettingViewModel
import com.api.palette.presentation.util.changeFragment
import com.api.palette.presentation.util.log
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        setupListeners()
        loadUserNameInfo()
        return binding.root
    }

    private fun setupListeners() = with(binding) {
        llLogout.setOnClickListener { showLogoutDialog() }
        llResign.setOnClickListener { showResignDialog(requireContext()) }
        llPrivacyPolicy.setOnClickListener { openWebPage(PRIVACY_POLICY_URL) }
        llAppInfo.setOnClickListener { openWebPage(APP_INFO_URL) }
        llMy.setOnClickListener { changeFragment(MyInfoFragment()) }
    }

    private fun loadUserNameInfo() {
        val prefs = PaletteApplication.prefs
        binding.tvUserName.text = prefs.username

        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.getProfile(prefs.token) { data ->
                data?.let { profile ->
                    binding.tvUserName.text = profile.name
                    prefs.username = profile.name
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).setCancelable(false).create()

        dialogView.findViewById<TextView>(R.id.noLogoutTextView).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.logoutTextView).setOnClickListener {
            lifecycleScope.launch {
                settingViewModel.logout(PaletteApplication.prefs.token)
            }
            PaletteApplication.prefs.clearToken()
            PaletteApplication.prefs.clearUser()

            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showResignDialog(context: Context) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_resign, null)
        val dialog = AlertDialog.Builder(context).setView(dialogView).setCancelable(false).create()

        dialogView.findViewById<TextView>(R.id.noTextView).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.yesTextView).setOnClickListener {
            resign()
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as? Activity)?.finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun resign() {
        lifecycleScope.launch {
            try {
                val response = settingViewModel.resign(PaletteApplication.prefs.token)
                if (!response.isSuccessful) {
                    log("Resign failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다.")
                log(e.stackTraceToString())
            } catch (e: Exception) {
                shortToast("네트워크 오류가 발생했습니다.")
                log(e.stackTraceToString())
            }
        }
    }

    private fun openWebPage(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            log(e.stackTraceToString())
        }
    }

    companion object {
        private const val PRIVACY_POLICY_URL = "https://dgsw-team-api.notion.site/cc32c87f614e4798893293abfe5ca72a"
        private const val APP_INFO_URL = "https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"
    }
}
