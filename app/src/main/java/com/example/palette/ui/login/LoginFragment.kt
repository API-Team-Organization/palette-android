package com.example.palette.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.common.Constant.TAG
import com.example.palette.common.HeaderUtil
import com.example.palette.data.auth.AuthRequestManager
import com.example.palette.data.auth.LoginRequest
import com.example.palette.databinding.FragmentLoginBinding
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private lateinit var email: String
    private lateinit var pw: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            loginButton.setOnClickListener {
                if (loginEmailEdit.text.isEmpty()) {
                    handleLoginFailure(loginEmailEdit)
                    binding.emailFailedText.visibility = View.VISIBLE
                    passwordFailedText.visibility = View.GONE
                    loginPasswordEdit.background = ContextCompat.getDrawable(loginPasswordEdit.context, R.drawable.bac_object)
                    return@setOnClickListener
                }
                if (loginPasswordEdit.text.isEmpty()) {
                    handleLoginFailure(loginPasswordEdit)
                    binding.passwordFailedText.visibility = View.VISIBLE
                    emailFailedText.visibility = View.GONE
                    loginEmailEdit.background = ContextCompat.getDrawable(loginEmailEdit.context, R.drawable.bac_object)
                    return@setOnClickListener
                }
                login()
                loginRequest()
            }
            loginToJoin.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
            }
        }
    }

    private fun login() {
        with(binding) {
            emailFailedText.visibility = View.GONE
            loginEmailEdit.background = ContextCompat.getDrawable(binding.loginEmailEdit.context, R.drawable.bac_object)
            passwordFailedText.visibility = View.GONE
            loginPasswordEdit.background = ContextCompat.getDrawable(binding.loginPasswordEdit.context, R.drawable.bac_object)
        }
        // 서버랑 통신 해서 이메일 존재 여부, 비밀 번호 일치 등 확인 후, 로그인 성공 Toast
    }

    private fun handleLoginFailure(loginText: EditText) {
        loginText.background = ContextCompat.getDrawable(loginText.context, R.drawable.bac_edit_text_failed)
        loginText.requestFocus()
        loginText.selectAll()
    }

    private fun loginRequest() {
        email = binding.loginEmailEdit.text.toString()
        pw = binding.loginPasswordEdit.text.toString()

        val loginRequest = LoginRequest(email, pw)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthRequestManager.loginRequest(loginRequest)
                Log.d(TAG, "response.header : ${response.code()}")

                val token = response.headers()[HeaderUtil.X_AUTH_TOKEN]
                Log.d(TAG, "token is $token")
                PaletteApplication.prefs.token = token ?: ""
                shortToast("로그인 성공")
                val intent = Intent(activity, ServiceActivity::class.java)
                requireActivity().startActivity(intent)
                activity?.finish()

            } catch (e: HttpException) {
                shortToast("이메일과 비밀번호를 다시 확인해주세요")
            } catch (e: SocketTimeoutException) {
                shortToast("네트워크 연결이 불안정합니다. 다시 시도해주세요.")
            } catch (e: Exception) {
                // HTTP 오류가 아닌 다른 예외가 발생한 경우에 대한 처리
                Log.e("LoginFragment", "loginRequest error", e)
                shortToast("알 수 없는 에러")
            }
        }
    }
}