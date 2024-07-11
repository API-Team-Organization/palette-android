package com.example.palette.ui.register

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant.TAG
import com.example.palette.common.HeaderUtil
import com.example.palette.data.auth.RegisterRequest
import com.example.palette.data.auth.RegisterRequestManager
import com.example.palette.databinding.FragmentJoinCompleteBinding
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class JoinCompleteFragment : Fragment() {
    private lateinit var binding : FragmentJoinCompleteBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinCompleteBinding.inflate(inflater, container, false)

        goodGradation()

        binding.btnStart.setOnClickListener {
            registerRequest()
            findNavController().navigate(R.id.action_joinCompleteFragment_to_joinCheckNumFragment)
        }

        return binding.root
    }

    private fun goodGradation() {
        val good = binding.good
        val textShader = LinearGradient(0f, 0f, 100f, 100f,
            intArrayOf(Color.parseColor("#6389E9"), Color.parseColor("#555FE8")),
            floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )

        good.paint.shader = textShader
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
                Log.d(TAG, "email: ${it.email}")
                Log.d(TAG, "password: ${it.password}")
                Log.d(TAG, "birthDate: ${it.birthDate}")
                Log.d(TAG, "username: ${it.username}")

                val supervisorJob = SupervisorJob()
                viewLifecycleOwner.lifecycleScope.launch(supervisorJob) {
                    try {
                        val response = RegisterRequestManager.registerRequest(request)
                        Log.d(TAG, "response.header : ${response.code()}")

                        val token = response.headers()[HeaderUtil.X_AUTH_TOKEN]
                        Log.d(TAG, "token is $token")

                        PaletteApplication.prefs.token = token ?: ""
                        shortToast("회원가입 성공, 이메일 인증을 진행해주세요.")

                    } catch (e: SocketTimeoutException) {
                        Log.e(TAG, "Network timeout", e)
                        shortToast("네트워크 연결 시간 초과")

                    } catch (e: HttpException) {
                        Log.e(TAG, "HTTP error: ${e.code()}", e)
                        Log.e(TAG, "HTTP error: ${e.response()?.raw()?.request()}", e)
                        shortToast("http 문제 발생")
                        findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)

                    } catch (e: Exception) {
                        Log.e(TAG, "알 수 없는 오류 발생", e)
                        shortToast("알 수 없는 오류 발생")
                    }
                }
            } ?: run {
                // registerRequest가 null인 경우에 대한 처리
                Log.e(TAG, "registerRequest is null")
                shortToast("회원가입 데이터를 가져오는 데 실패했습니다")
            }
        }
    }
}