package com.example.palette.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.application.UserPrefs
import com.example.palette.common.Constant
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch

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

        binding.llMy.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, MyInfoFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    private fun loadUserNameInfo() {
        binding.tvUserName.text = UserPrefs.userName

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                profileInfo?.data?.let { data ->
                    binding.tvUserName.text = data.name
                    UserPrefs.userName = data.name
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting UserNameInfo error : ", e)
            }
        }
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = AuthRequestManager.logoutRequest(PaletteApplication.prefs.token)
            Log.d(Constant.TAG, "Logout response.header code : ${response.code()}")
        }

        PaletteApplication.prefs = PreferenceManager(requireContext().applicationContext)
        PaletteApplication.prefs.clearToken()
        UserPrefs.clearUserData()

        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

        activity?.finish()
    }
}