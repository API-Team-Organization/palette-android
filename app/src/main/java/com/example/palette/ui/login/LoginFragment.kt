package com.example.palette.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
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
import com.example.palette.data.error.CustomException
import com.example.palette.databinding.FragmentLoginBinding
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var email: String
    private lateinit var pw: String
    private var backPressedTime: Long = 0L
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initView()
        handleOnBackPressed()

        changeEditTextFocusColor(binding.etLoginEmail)
        changeEditTextFocusColor(binding.etLoginPassword)

        return binding.root
    }

    private fun initView() {
        with(binding) {
            btnLogin.setOnClickListener {
                if (etLoginEmail.text.isEmpty()) {
                    handleLoginFailure(etLoginEmail)
                    binding.emailFailedText.visibility = View.VISIBLE
                    passwordFailedText.visibility = View.GONE
                    etLoginPassword.background = ContextCompat.getDrawable(etLoginPassword.context, R.drawable.bac_object)
                    return@setOnClickListener
                }
                if (etLoginPassword.text.isEmpty()) {
                    handleLoginFailure(etLoginPassword)
                    binding.passwordFailedText.visibility = View.VISIBLE
                    emailFailedText.visibility = View.GONE
                    etLoginEmail.background = ContextCompat.getDrawable(etLoginEmail.context, R.drawable.bac_object)
                    return@setOnClickListener
                }
                loginRequest()
            }
            tvRegister.setOnClickListener {
                disableOnBackPressedCallback() // 뒤로가기 콜백 비활성화
                findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
            }
        }
    }

    private fun changeEditTextFocusColor(editTextId: EditText) {
        editTextId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextId.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                editTextId.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }
    }

    private fun handleLoginFailure(loginText: EditText) {
        loginText.background = ContextCompat.getDrawable(loginText.context, R.drawable.bac_edit_text_failed)
        loginText.requestFocus()
        loginText.selectAll()
    }

    private fun loginRequest() {
        email = binding.etLoginEmail.text.toString()
        pw = binding.etLoginPassword.text.toString()

        val loginRequest = LoginRequest(email, pw)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthRequestManager.loginRequest(loginRequest)
                Log.d(TAG, "LoginFragment response : $response")
                val token = response.headers()[HeaderUtil.X_AUTH_TOKEN]
                Log.d(TAG, "token is $token")
                PaletteApplication.prefs.token = token ?: ""
                shortToast("로그인 성공")
                val intent = Intent(activity, ServiceActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            } catch (e: CustomException) {
                shortToast(e.errorResponse.message)
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

    private fun handleOnBackPressed() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime <= 2000) {
                    activity?.finish()
                } else {
                    backPressedTime = System.currentTimeMillis()
                    shortToast("한 번 더 누르면 종료됩니다.")
                }
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.remove() // 뷰가 파괴될 때 콜백 해제
    }

    private fun disableOnBackPressedCallback() {
        callback.isEnabled = false
    }
}
