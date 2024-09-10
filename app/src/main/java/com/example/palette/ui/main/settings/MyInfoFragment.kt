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
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.changeFragment
import kotlinx.coroutines.launch

class MyInfoFragment : Fragment() {

    private lateinit var binding: FragmentMyInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.GONE

        initView()
        loadProfileInfo()

        return binding.root
    }

    private fun initView() {
        binding.llUsername.setOnClickListener {
            changeFragment(ChangeNameFragment())
        }

        binding.llBirthdate.setOnClickListener {
            changeFragment(ChangeBirthDateFragment())
        }

        binding.llPassword.setOnClickListener {
            changeFragment(ChangePasswordFragment())
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

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}