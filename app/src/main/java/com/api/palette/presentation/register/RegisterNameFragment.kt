package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.common.HeaderUtil
import com.api.palette.data.auth.request.RegisterRequest
import com.api.palette.databinding.FragmentJoinNameBinding
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import com.api.palette.presentation.util.shortToast
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

class RegisterNameFragment : Fragment() {

    private var _binding: FragmentJoinNameBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        with(binding) {
            btnComplete.setOnClickListener { validateName() }
            etJoinName.setOnFocusChangeListener { _, hasFocus ->
                etJoinName.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(),
                    if (hasFocus) R.color.blue else R.color.black
                )
            }
        }
    }

    private fun validateName() {
        val name = binding.etJoinName.text.toString()
        when {
            name.isBlank() -> showNameError(isFormatError = false)
            !isValidKoreanName(name) -> showNameError(isFormatError = true)
            else -> {
                registerViewModel.setUsername(name)
                registerUser()
            }
        }
    }

    private fun showNameError(isFormatError: Boolean) {
        with(binding) {
            etJoinName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            etJoinName.requestFocus()
            etJoinName.selectAll()
            failedNameEmpty.visibility = if (isFormatError) View.GONE else View.VISIBLE
            failedNameFormat.visibility = if (isFormatError) View.VISIBLE else View.GONE
        }
    }

    private fun registerUser() {
        val data = registerViewModel.toRegisterRequest()
        if (data == null) {
            shortToast("회원가입 정보를 불러올 수 없습니다.")
            return
        }

        val request = RegisterRequest(
            email = data.email,
            password = data.password,
            birthDate = data.birthDate,
            username = data.username
        )

        lifecycleScope.launch(SupervisorJob()) {
            try {
                val response = com.api.palette.data.auth.AuthRequestManager.registerRequest(request)
                if (!response.isSuccessful) {
                    shortToast("이미 존재하는 이메일입니다.")
                    return@launch
                }

                val token = response.headers()[HeaderUtil.X_AUTH_TOKEN]
                PaletteApplication.prefs.token = token.orEmpty()
                shortToast("이메일 인증을 진행해주세요.")
                findNavController().navigate(R.id.action_joinNameFragment_to_joinCheckNumFragment)

            } catch (e: SocketTimeoutException) {
                shortToast("요청 시간이 초과되었습니다.")
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다.")
            } catch (e: Exception) {
                shortToast("예기치 않은 오류가 발생했습니다.")
            }
        }
    }

    private fun isValidKoreanName(name: String): Boolean {
        return Pattern.matches("^[가-힣]+$", name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
