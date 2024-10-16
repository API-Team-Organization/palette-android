package com.api.palette.ui.register

import android.os.Bundle
import android.util.Log
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
import com.api.palette.common.Constant
import com.api.palette.common.HeaderUtil
import com.api.palette.data.auth.AuthRequestManager
import com.api.palette.data.auth.RegisterRequest
import com.api.palette.databinding.FragmentJoinNameBinding
import com.api.palette.ui.util.shortToast
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

class JoinNameFragment : Fragment() {
    private lateinit var binding : FragmentJoinNameBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinNameBinding.inflate(inflater, container, false)

        binding.btnComplete.setOnClickListener {
            checkName()
        }

        binding.etJoinName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etJoinName.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                binding.etJoinName.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        return binding.root
    }

    private fun checkName() {
        with(binding) {
            val name = etJoinName.text.toString()

            if (name.isEmpty()) {
                checkNameFailed(etJoinName)
                failedNameEmpty.visibility = View.VISIBLE
                failedNameFormat.visibility = View.GONE
            } else {
                val isNameValid = nameRegularExpression(name)
                Log.d("isNameValid", "${nameRegularExpression(name)}")

                if (isNameValid) {
                    registerViewModel.username.observe(viewLifecycleOwner) {
                        binding.etJoinName.setText(
                            it
                        )
                    }
                    registerViewModel.setUsername(binding.etJoinName.text.toString())
                    registerRequest()
                    findNavController().navigate(R.id.action_joinNameFragment_to_joinCheckNumFragment)
                } else {
                    checkNameFailed(etJoinName)
                    failedNameFormat.visibility = View.VISIBLE
                    failedNameEmpty.visibility = View.GONE
                }
            }
        }
    }


    private fun registerRequest() {
        // ViewModel에서 데이터를 observe하여 가져옵니다.
        registerViewModel.getRegisterRequestData().observe(viewLifecycleOwner) { registerRequest ->
            // observe에서 새로운 데이터가 전달되었을 때만 동작합니다.
            registerRequest?.let {
                val request = RegisterRequest(
                    email = it.email,
                    password = it.password,
                    birthDate = it.birthDate,
                    username = it.username,
                )
                Log.d(Constant.TAG, "email: ${it.email}")
                Log.d(Constant.TAG, "password: ${it.password}")
                Log.d(Constant.TAG, "birthDate: ${it.birthDate}")
                Log.d(Constant.TAG, "username: ${it.username}")

                val supervisorJob = SupervisorJob()
                viewLifecycleOwner.lifecycleScope.launch(supervisorJob) {
                    try {
                        val response = AuthRequestManager.registerRequest(request)
                        Log.d(Constant.TAG, "response.header : ${response.headers()}")
                        Log.d(Constant.TAG, "response.code : ${response.code()}")
                        Log.d(Constant.TAG, "response : $response")
                        Log.d(Constant.TAG, "response.message : ${response.code()}")
                        Log.d(Constant.TAG, "response.body : ${response.body()}")

                        if (!response.isSuccessful) {
                            shortToast("중복된 이메일이 존재합니다")
                            Log.d(Constant.TAG, response.message())
                            return@launch
                        }
                        val token = response.headers()[HeaderUtil.X_AUTH_TOKEN]
                        Log.d(Constant.TAG, "token is $token")

                        PaletteApplication.prefs.token = token ?: ""
                        shortToast("이메일 인증을 진행해주세요")

                    } catch (e: SocketTimeoutException) {
                        Log.e(Constant.TAG, "Network timeout", e)
                        shortToast("네트워크 연결 시간 초과")

                    } catch (e: HttpException) {
                        Log.e(Constant.TAG, "HTTP error: ${e.code()}", e)
                        shortToast("http 문제 발생")
                        findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)

                    } catch (e: Exception) {
                        Log.e(Constant.TAG, "알 수 없는 오류 발생", e)
                        shortToast("알 수 없는 오류 발생")
                    }
                }
            } ?: run {
                // registerRequest가 null인 경우에 대한 처리
                Log.e(Constant.TAG, "registerRequest is null")
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