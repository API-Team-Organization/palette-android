package com.example.palette.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.common.Constant
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch


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
        with(binding) {
            profile.setOnClickListener {
                showProfileInfo()
            }

            logout.setOnClickListener {
                logout()
            }
        }
    }

    private fun showProfileInfo() {
        changeFragment(ProfileFragment())
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            val response = AuthRequestManager.logoutRequest()
            Log.d(Constant.TAG, "Logout response.header code : ${response.code()}")
        }

        PaletteApplication.prefs = PreferenceManager(requireContext().applicationContext)

        PaletteApplication.prefs.clearToken()
        val intent = Intent(activity, MainActivity::class.java)
        requireActivity().startActivity(intent)

        activity?.finish()
    }

    private fun changeFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainContent, fragment)
            .addToBackStack(null) // 백 스택에 프래그먼트 추가
            .commitAllowingStateLoss()
    }

}