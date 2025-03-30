package com.api.palette.presentation.register

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.presentation.register.viewmodel.RegisterCheckNumViewModel
import com.api.palette.presentation.util.changeFragment
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterCheckNumFragment : Fragment() {
    private var _binding: FragmentJoinCheckNumBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterCheckNumViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nEtJoinEmail.setText(PaletteApplication.prefs.userId)

        binding.etJoinCheckNum.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinCheckNum.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }

        binding.btnCheckNum.setOnClickListener {
            val code = binding.etJoinCheckNum.text.toString()
            if (code.isBlank()) {
                shortToast("인증번호를 입력해주세요.")
                return@setOnClickListener
            }
            viewModel.verifyCode(PaletteApplication.prefs.token, code)
        }

        binding.tvResend.setOnClickListener {
            viewModel.resendCode(PaletteApplication.prefs.token)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.verifyResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                changeFragment(RegisterCompleteFragment())
            }.onFailure {
                shortToast(it.message ?: "인증 실패")
            }
        }

        viewModel.resendResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                shortToast("인증번호가 재전송되었습니다.")
            }.onFailure {
                shortToast(it.message ?: "재전송 실패")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
