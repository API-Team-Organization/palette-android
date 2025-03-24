package com.api.palette.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.VerifyRequest
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import androidx.core.content.ContextCompat

class JoinCheckNumFragment : Fragment() {
    private lateinit var binding: FragmentJoinCheckNumBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)

        showEmail()

        binding.btnCheckNum.setOnClickListener {
            val verificationCode = binding.etJoinCheckNum.text.toString()
            verifyCode(verificationCode)
        }

        binding.tvResend.setOnClickListener { resendCode() }

        binding.etJoinCheckNum.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinCheckNum.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        return binding.root
    }

    private fun showEmail() {
        val email = PaletteApplication.prefs.userId
        binding.nEtJoinEmail.setText(email)
    }

    private fun verifyCode(code: String) {
        lifecycleScope.launch {
            try {
                val verifyData = VerifyRequest(code)
                val response = com.api.palette.data.auth.AuthRequestManager.verifyRequest(PaletteApplication.prefs.token, verifyData)
                if (response.isSuccessful) {
                    findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinCompleteFragment)
                } else {
                    shortToast("인증번호가 일치하지 않습니다.")
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
                val token = PaletteApplication.prefs.token
                val response = com.api.palette.data.auth.AuthRequestManager.resendRequest(token)
                if (response.isSuccessful) {
                    shortToast("인증번호가 재전송되었습니다")
                } else {
                    shortToast("인증번호 재전송에 실패했습니다.")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다")
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
            }
        }
    }
}
