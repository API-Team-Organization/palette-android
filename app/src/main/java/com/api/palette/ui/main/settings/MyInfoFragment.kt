package com.api.palette.ui.main.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.common.Constant
import com.api.palette.data.info.InfoRequestManager
import com.api.palette.databinding.FragmentMyInfoBinding
import com.api.palette.ui.base.BaseControllable
import com.api.palette.ui.main.ServiceActivity
import com.api.palette.ui.util.changeFragment
import kotlinx.coroutines.launch

class MyInfoFragment : Fragment() {

    private lateinit var binding: FragmentMyInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        (requireActivity() as? BaseControllable)?.bottomVisible(false)

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
        val prefs = PaletteApplication.prefs
        binding.tvEmail.text = prefs.userId
        binding.tvUserName.text = prefs.username
        binding.tvBirthDate.text = prefs.userBirthDate

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profileInfo = InfoRequestManager.profileInfoRequest(PaletteApplication.prefs.token)
                profileInfo?.data?.let { data ->
                    binding.tvEmail.text = data.email
                    binding.tvUserName.text = data.name
                    binding.tvBirthDate.text = data.birthDate

                    prefs.userId = data.email
                    prefs.username = data.name
                    prefs.userBirthDate = data.birthDate
                }
            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting profileInfo error : ", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}