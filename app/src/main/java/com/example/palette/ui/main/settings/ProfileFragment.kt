package com.example.palette.ui.main.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        initView()

        return binding.root
    }

    private fun initView() {
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d(Constant.TAG, "token : ${PaletteApplication.prefs.token}")


            try {
                val profileInfo = InfoRequestManager.requestProfileInfo(PaletteApplication.prefs.token)
                Log.d(Constant.TAG, "profileInfo: ${profileInfo!!.data}")

                binding.profileEmail.text = profileInfo.data.email
                binding.profileName.text = profileInfo.data.name
                binding.profileBirthDate.text = profileInfo.data.birthDate


            } catch (e: Exception) {
                Log.e(Constant.TAG, "Setting profileInfo error : ",e)
            }

        }
    }

}