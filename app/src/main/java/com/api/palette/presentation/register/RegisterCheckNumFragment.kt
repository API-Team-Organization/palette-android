package com.api.palette.presentation.register

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.request.VerifyRequest
import com.api.palette.databinding.FragmentJoinCheckNumBinding
import com.api.palette.domain.auth.usecase.ResendUseCase
import com.api.palette.domain.auth.usecase.VerifyUseCase
import com.api.palette.presentation.util.changeFragment
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class RegisterCheckNumFragment : Fragment() {
    private var _binding: FragmentJoinCheckNumBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var verifyUseCase: VerifyUseCase
    @Inject lateinit var resendUseCase: ResendUseCase

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
            verifyCode(code)
        }
        binding.tvResend.setOnClickListener {
            resendCode()
        }
    }

    private fun verifyCode(code: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = verifyUseCase(PaletteApplication.prefs.token, VerifyRequest(code))
                if (response.isSuccessful) {
                    changeFragment(RegisterCompleteFragment())
                } else {
                    shortToast("인증 실패: ${response.code()}")
                }
            } catch (e: HttpException) {
                shortToast("서버오류 발생: ${e.code()}")
            } catch (e: Exception) {
                shortToast("알 수 없는 오류: ${e.message}")
            }
        }
    }

    private fun resendCode() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = resendUseCase(PaletteApplication.prefs.token)
                if (response.isSuccessful) {
                    shortToast("인증번호가 재전송되었습니다.")
                } else {
                    shortToast("재전송 실패: ${response.code()}")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다: ${e.code()}")
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
