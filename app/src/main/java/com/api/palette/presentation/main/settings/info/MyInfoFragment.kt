package com.api.palette.presentation.main.settings.info

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentMyInfoBinding
import com.api.palette.domain.info.usecase.GetProfileInfoUseCase
import com.api.palette.presentation.main.settings.editprofile.ChangeBirthDateFragment
import com.api.palette.presentation.main.settings.editprofile.ChangeNameFragment
import com.api.palette.presentation.main.settings.editprofile.ChangePasswordFragment
import com.api.palette.presentation.util.changeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyInfoFragment : Fragment() {
    private lateinit var binding: FragmentMyInfoBinding

    @Inject lateinit var getProfileInfoUseCase: GetProfileInfoUseCase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
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
            runCatching {
                getProfileInfoUseCase(prefs.token)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        binding.tvEmail.text = data.email
                        binding.tvUserName.text = data.name
                        binding.tvBirthDate.text = data.birthDate
                        prefs.userId = data.email
                        prefs.username = data.name
                        prefs.userBirthDate = data.birthDate
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<View>(R.id.bottomBar)?.visibility = View.VISIBLE
    }
}
