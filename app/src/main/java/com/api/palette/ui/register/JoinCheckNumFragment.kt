package com.api.palette.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.AuthRequestManager
import com.api.palette.data.auth.VerifyRequest
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class JoinCheckNumFragment : Fragment() {
    private lateinit var binding: FragmentJoinCheckNumBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)

        showEmail()

        binding.btnCheckNum.setOnClickListener {
            val verificationCode = binding.etJoinCheckNum.text.toString()
            verifyCode(verificationCode)
        }

        binding.tvResend.setOnClickListener {
            resendCode()
        }

        binding.etJoinCheckNum.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etJoinCheckNum.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                binding.etJoinCheckNum.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        return binding.root
    }

    private fun showEmail() {
        val email = PaletteApplication.prefs.userId
        binding.nEtJoinEmail.setText(email)
    }

    private fun verifyCode(code: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val verifyData = VerifyRequest(code)
                try {
                    AuthRequestManager.verifyRequest(PaletteApplication.prefs.token, verifyData)
                } catch (e: CustomException) {
                    when (e.errorResponse.kind) {
                        "INVALID_VERIFY_CODE" -> shortToast("인증번호가 일치하지 않습니다.")
                        else -> shortToast("알수는 있는데 일단 안드가 잘못한 오류: ${e.errorResponse.message}")
                    }
                    null
                } ?: return@launch

                findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinCompleteFragment)
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다")
                Log.d("JoinCheckNumFragment", e.stackTraceToString())
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
            }
        }
    }

    private fun resendCode() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = PaletteApplication.prefs.token
                Log.d("JoinCheckNumFragment", "Token: $token")

                val response = AuthRequestManager.resendRequest(token)
                Log.d(
                    "JoinCheckNumFragment",
                    "Response code: ${response.code()}, Response body: ${response.body()}"
                )

                if (response.isSuccessful) {
                    shortToast("인증번호가 재전송되었습니다")
                } else {
                    shortToast("인증번호 재전송에 실패했습니다.")
                    Log.d(
                        "JoinCheckNumFragment",
                        "Response code: ${response.code()}, Response message: ${response.message()}"
                    )
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다")
                Log.e("JoinCheckNumFragment", "HTTP Exception: ${e.message}", e)
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
                Log.e("JoinCheckNumFragment", "Exception: ${e.message}", e)
            }
        }
    }
}
