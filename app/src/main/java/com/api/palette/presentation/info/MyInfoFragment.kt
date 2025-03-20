package com.api.palette.presentation.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentMyInfoBinding
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInfoFragment : Fragment() {

    private var _binding: FragmentMyInfoBinding? = null
    private val binding get() = _binding!!
    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyInfoBinding.inflate(inflater, container, false)
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        observeViewModel()
        loadProfileInfo()
        return binding.root
    }

    private fun loadProfileInfo() {
        infoViewModel.loadProfile(PaletteApplication.prefs.token)
    }

    private fun observeViewModel() {
        infoViewModel.profileData.observe(viewLifecycleOwner) { data ->
            binding.tvEmail.text = data.email
            binding.tvUserName.text = data.name
            binding.tvBirthDate.text = data.birthDate
            PaletteApplication.prefs.userId = data.email
            PaletteApplication.prefs.username = data.name
            PaletteApplication.prefs.userBirthDate = data.birthDate
        }
        infoViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
