package com.example.palette.ui.register

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.auth.VerifyRequest
import com.example.palette.databinding.FragmentJoinCheckNumBinding
import com.example.palette.ui.util.log
import com.example.palette.ui.util.shortToast
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
                val response = AuthRequestManager.verifyRequest(PaletteApplication.prefs.token, verifyData)
                Log.d("JoinCheckNumFragment", response.body().toString())

                if (response.isSuccessful) {
                    findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinCompleteFragment)
                } else {
                    shortToast("인증번호가 일치하지 않습니다.")
                    log("JoinCheckNumFragment verifyCode response: $response")
                }
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
                Log.d("JoinCheckNumFragment", "Response code: ${response.code()}, Response body: ${response.body()}")

                if (response.isSuccessful) {
                    shortToast("인증번호가 재전송되었습니다.")
                } else {
                    shortToast("인증번호 재전송에 실패했습니다.")
                    Log.d("JoinCheckNumFragment", "Response code: ${response.code()}, Response message: ${response.message()}")
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
