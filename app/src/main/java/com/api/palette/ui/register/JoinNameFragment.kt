package com.api.palette.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentJoinNameBinding
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.RegisterViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

class JoinNameFragment : Fragment() {
    private lateinit var binding: FragmentJoinNameBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentJoinNameBinding.inflate(inflater, container, false)
        binding.btnComplete.setOnClickListener { checkName() }
        binding.etJoinName.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        return binding.root
    }

    private fun checkName() {
        val name = binding.etJoinName.text.toString()
        if (name.isEmpty()) {
            checkNameFailed(binding.etJoinName)
            binding.failedNameEmpty.visibility = View.VISIBLE
            binding.failedNameFormat.visibility = View.GONE
        } else {
            val isNameValid = nameRegularExpression(name)
            if (isNameValid) {
                registerViewModel.setUsername(name)
                registerRequest()
                findNavController().navigate(R.id.action_joinNameFragment_to_joinCheckNumFragment)
            } else {
                checkNameFailed(binding.etJoinName)
                binding.failedNameFormat.visibility = View.VISIBLE
                binding.failedNameEmpty.visibility = View.GONE
            }
        }
    }

    private fun registerRequest() {
        registerViewModel.getRegisterRequestData().observe(viewLifecycleOwner) { registerRequest ->
            registerRequest?.let {
                val request = com.api.palette.data.auth.RegisterRequest(
                    email = it.email,
                    password = it.password,
                    birthDate = it.birthDate,
                    username = it.username,
                )
                val supervisorJob = SupervisorJob()
                viewLifecycleOwner.lifecycleScope.launch(supervisorJob) {
                    try {
                        val response = com.api.palette.data.auth.AuthRequestManager.registerRequest(request)
                        if (!response.isSuccessful) {
                            shortToast("중복된 이메일이 존재합니다")
                            return@launch
                        }
                        val token = response.headers()["X-AUTH-Token"] ?: ""
                        PaletteApplication.prefs.token = token
                        shortToast("이메일 인증을 진행해주세요")
                    } catch (e: SocketTimeoutException) {
                        shortToast("네트워크 연결 시간 초과")
                    } catch (e: HttpException) {
                        shortToast("http 문제 발생")
                        findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
                    } catch (e: Exception) {
                        shortToast("알 수 없는 오류 발생")
                    }
                }
            } ?: run {
                shortToast("회원가입 데이터를 가져오는 데 실패했습니다")
            }
        }
    }

    private fun checkNameFailed(name: EditText) {
        name.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        name.requestFocus()
        name.selectAll()
    }

    private fun nameRegularExpression(name: String): Boolean {
        val namePattern = "^[가-힣]*\$"
        val pattern = Pattern.compile(namePattern)
        val matcher = pattern.matcher(name)
        return matcher.find()
    }
}
