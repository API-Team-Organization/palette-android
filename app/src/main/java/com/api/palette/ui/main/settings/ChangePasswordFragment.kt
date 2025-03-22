package com.api.palette.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.api.palette.R
import com.api.palette.databinding.FragmentChangePasswordBinding
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.AuthViewModel

class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        (activity as? com.api.palette.ui.main.ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.GONE
        initView()
        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        authViewModel.passwordChangeResponse.observe(viewLifecycleOwner) { success ->
            if (success) {
                shortToast("비밀번호가 성공적으로 변경되었습니다.")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        authViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                shortToast(error)
            }
        }

        binding.changePasswordBtn.setOnClickListener {
            val beforePassword = binding.etBeforePassword.text.toString().trim()
            val afterPassword = binding.etAfterPassword.text.toString().trim()
            if (beforePassword.isEmpty() || afterPassword.isEmpty()) {
                shortToast("이전 비밀번호와 변경할 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }
            authViewModel.changePassword(beforePassword, afterPassword)
        }
        return binding.root
    }

    private fun initView() {
        binding.etBeforePassword.setOnFocusChangeListener { _, hasFocus ->
            binding.etBeforePassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        binding.etAfterPassword.setOnFocusChangeListener { _, hasFocus ->
            binding.etAfterPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.blue)
        }
    }
}
