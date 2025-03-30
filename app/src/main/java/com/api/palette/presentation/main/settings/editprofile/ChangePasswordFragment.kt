package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.R
import com.api.palette.databinding.FragmentChangePasswordBinding
import com.api.palette.presentation.main.settings.editprofile.viewmodel.ChangePasswordViewModel
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        setupFocusListeners()
        setupBackButton()
        setupSubmitButton()
        return binding.root
    }

    private fun setupFocusListeners() {
        setFocusColor(binding.etBeforePassword)
        setFocusColor(binding.etAfterPassword)
    }

    private fun setFocusColor(view: android.widget.EditText) {
        view.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            view.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
        }
    }

    private fun setupBackButton() {
        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSubmitButton() {
        binding.changePasswordBtn.setOnClickListener {
            val beforePassword = binding.etBeforePassword.text.toString().trim()
            val afterPassword = binding.etAfterPassword.text.toString().trim()

            if (beforePassword.isEmpty() || afterPassword.isEmpty()) {
                shortToast("이전 비밀번호와 변경할 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }

            changePasswordViewModel.changePassword(beforePassword, afterPassword) { result ->
                if (result.isSuccess) {
                    shortToast("비밀번호가 성공적으로 변경되었습니다.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("비밀번호 변경에 실패했습니다.")
                }
            }
        }
    }
}
