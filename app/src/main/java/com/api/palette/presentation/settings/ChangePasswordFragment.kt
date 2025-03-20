package com.api.palette.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentChangePasswordBinding
import com.api.palette.presentation.auth.ChangePasswordViewModel
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.changePasswordBtn.setOnClickListener {
            val beforePassword = binding.etBeforePassword.text.toString().trim()
            val afterPassword = binding.etAfterPassword.text.toString().trim()
            if (beforePassword.isEmpty() || afterPassword.isEmpty()) {
                shortToast("이전 비밀번호와 변경할 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }
            changePasswordViewModel.changePassword(PaletteApplication.prefs.token, beforePassword, afterPassword)
        }
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        changePasswordViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                shortToast("비밀번호가 성공적으로 변경되었습니다.")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        changePasswordViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            shortToast(error)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
