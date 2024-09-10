package com.example.palette.ui.main.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.UserPrefs
import com.example.palette.common.Constant
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentMyInfoBinding
import kotlinx.coroutines.launch

class MyInfoFragment : Fragment() {

    private lateinit var binding: FragmentMyInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInfoBinding.inflate(inflater, container, false)

        loadProfileInfo()
        screenNavigation()

        return binding.root
    }

    private fun screenNavigation() {
        binding.llUsername.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, ChangeNameFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.llBirthdate.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, ChangeBirthDateFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.llPassword.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainContent, ChangePasswordFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    private fun loadProfileInfo() {
        binding.tvEmail.text = UserPrefs.userId
        binding.tvUserName.text = UserPrefs.userName
        binding.tvBirthDate.text = UserPrefs.userBirthDate

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                profileInfo?.data?.let { data ->
                    binding.tvEmail.text = data.email
                    binding.tvUserName.text = data.name
                    binding.tvBirthDate.text = data.birthDate

                    UserPrefs.userId = data.email
                    UserPrefs.userName = data.name
                    UserPrefs.userBirthDate = data.birthDate
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting profileInfo error : ", e)
            }
        }
    }
}