package com.example.palette.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.common.Constant
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentSettingBinding
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding
//    private lateinit var anim: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
//        anim = AnimationUtils.loadAnimation(activity, R.anim.scale)
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d(Constant.TAG, "token : ${PaletteApplication.prefs.token}")

            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                Log.d(Constant.TAG, "profileInfo: ${profileInfo!!.data}")

                binding.userEmail.text = profileInfo.data.email
                binding.userName.text = profileInfo.data.name
                binding.userBirthday.text = profileInfo.data.birthDate

            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting profileInfo error : ",e)
            }
        }

        binding.logout.setOnClickListener {
            logout()
        }

        binding.appResign.setOnClickListener {
            resign()
        }

        binding.edit.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, EditUserInfoFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = AuthRequestManager.logoutRequest(PaletteApplication.prefs.token)
            Log.d(Constant.TAG, "Logout response.header code : ${response.code()}")
        }

        PaletteApplication.prefs = PreferenceManager(requireContext().applicationContext)

        PaletteApplication.prefs.clearToken()
        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

        activity?.finish()
    }

    private fun resign() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthRequestManager.resignRequest(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    // 회원 탈퇴 성공
                    shortToast("회원 탈퇴 성공")
                    Log.d(Constant.TAG, "Resign success")
                } else {
                    // 회원 탈퇴 실패
                    Log.e(Constant.TAG, "Resign failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: HttpException) {
                shortToast("HttpException")
                Log.e(Constant.TAG, "Resign HTTP error", e)
            } catch (e: Exception) {
                shortToast("Exception")
                Log.e(Constant.TAG, "Resign error", e)
            }
        }
    }
}