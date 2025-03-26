package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.AuthRequestManager
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.presentation.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterCheckNumFragment : Fragment() {

    private var _binding: FragmentJoinCheckNumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.nEtJoinEmail.setText(PaletteApplication.prefs.userId)

        binding.etJoinCheckNum.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            binding.etJoinCheckNum.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), color)
        }

        binding.btnCheckNum.setOnClickListener {
            val code = binding.etJoinCheckNum.text.toString()
            if (code.isBlank()) {
                shortToast("인증번호를 입력해주세요.")
                return@setOnClickListener
            }
            verifyCode(code)
        }

        binding.tvResend.setOnClickListener {
            resendCode()
        }
    }

    private fun verifyCode(code: String) {
        lifecycleScope.launch {
            try {
                val verifyRequest = VerifyRequest(code)
                AuthRequestManager.verifyRequest(PaletteApplication.prefs.token, verifyRequest)
                findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinCompleteFragment)
            } catch (e: CustomException) {
                when (e.errorResponse.kind) {
                    "INVALID_VERIFY_CODE" -> shortToast("인증번호가 일치하지 않습니다.")
                    else -> shortToast("오류: ${e.errorResponse.message}")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다")
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
            }
        }
    }

    private fun resendCode() {
        lifecycleScope.launch {
            try {
                val response = AuthRequestManager.resendRequest(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    shortToast("인증번호가 재전송되었습니다.")
                } else {
                    shortToast("인증번호 재전송에 실패했습니다.")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다.")
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
