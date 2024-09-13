package com.example.palette.ui.main.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.palette.MainActivity
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.common.Constant.TAG
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentSettingBinding
import com.example.palette.ui.util.changeFragment
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        loadUserNameInfo()

        binding.llLogout.setOnClickListener {
            logout()
        }

        binding.llResign.setOnClickListener {
            resignDialog(requireContext())
        }

        binding.llPrivacyPolicy.setOnClickListener {
            goToPrivacyPolicyPage()
        }

        binding.llAppInfo.setOnClickListener {
            goToAppInfoPage()
        }

        binding.llMy.setOnClickListener {
            changeFragment(MyInfoFragment())
        }

        return binding.root
    }

    private fun loadUserNameInfo() {
        val prefs = PaletteApplication.prefs
        binding.tvUserName.text = prefs.username

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                profileInfo?.data?.let { data ->
                    binding.tvUserName.text = data.name
                    prefs.username = data.name
                }
            } catch (e: Exception) {
                Log.e(TAG, "Setting UserNameInfo error : ", e)
            }
        }
    }

    private fun goToPrivacyPolicyPage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dgsw-team-api.notion.site/cc32c87f614e4798893293abfe5ca72a"))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    private fun goToAppInfoPage() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://4-rne5.notion.site/Team-API-2100356bfe554cf58df89b204b3afb8d"))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, e.stackTraceToString())
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = AuthRequestManager.logoutRequest(PaletteApplication.prefs.token)
            Log.d(TAG, "Logout response.header code : ${response.code()}")
        }

        PaletteApplication.prefs.clearToken()
        PaletteApplication.prefs.clearUser()

        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

        activity?.finish()
        log(PaletteApplication.prefs.token)
    }

    private fun resignDialog(context: Context) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle("회원탈퇴")
            setMessage("정말 탈퇴하시겠습니까?")
            setPositiveButton("탈퇴") { dialog, _ ->
                resign()
                val intent = Intent(activity, MainActivity::class.java)
                requireActivity().startActivity(intent)

                activity?.finish()
                dialog.dismiss()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }

        builder.create().show()
    }

    private fun resign() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthRequestManager.resignRequest(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    // 회원 탈퇴 성공
                    shortToast("회원 탈퇴 성공")
                    Log.d(TAG, "Resign success")
                } else {
                    // 회원 탈퇴 실패
                    Log.e(TAG, "Resign failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: HttpException) {
                shortToast("HttpException")
                Log.e(TAG, "Resign HTTP error", e)
            } catch (e: Exception) {
                shortToast("Exception")
                Log.e(TAG, "Resign error", e)
            }
        }
    }
}