package com.api.palette.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentChangeNameBinding
import com.api.palette.presentation.info.InfoViewModel
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNameFragment : Fragment() {

    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!
    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.changeNameBtn.setOnClickListener {
            val username = binding.etChangeName.text.toString().trim()
            if (username.isEmpty()) {
                shortToast("이름을 입력해주세요.")
                return@setOnClickListener
            }
            PaletteApplication.prefs.username = ""
            infoViewModel.changeName(PaletteApplication.prefs.token, username)
        }
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        infoViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                shortToast("프로필이 성공적으로 업데이트되었습니다.")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        infoViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
