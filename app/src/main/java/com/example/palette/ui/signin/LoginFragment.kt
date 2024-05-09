package com.example.palette.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentLoginBinding
import com.example.palette.ui.util.shortToast

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding

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
                    loginFailed(loginEmailEdit)
                    binding.emailFailedText.visibility = View.VISIBLE
                    passwordFailedText.visibility = View.GONE
                    loginPasswordEdit.background = ContextCompat.getDrawable(loginPasswordEdit.context, R.drawable.bac_edit_text)
                    return@setOnClickListener
                }
                if (loginPasswordEdit.text.isEmpty()) {
                    loginFailed(loginPasswordEdit)
                    binding.passwordFailedText.visibility = View.VISIBLE
                    emailFailedText.visibility = View.GONE
                    loginEmailEdit.background = ContextCompat.getDrawable(loginEmailEdit.context, R.drawable.bac_edit_text)
                    return@setOnClickListener
                }
                login()
            }
        }
    }

    private fun login() {
        with(binding) {
            emailFailedText.visibility = View.GONE
            loginEmailEdit.background = ContextCompat.getDrawable(binding.loginEmailEdit.context, R.drawable.bac_edit_text)
            passwordFailedText.visibility = View.GONE
            loginPasswordEdit.background = ContextCompat.getDrawable(binding.loginPasswordEdit.context, R.drawable.bac_edit_text)
        }

        // 서버랑 통신해서 이메일 존재여부, 비밀번호 일치 등 확인 후, 로그인 성공 Toast
        if (true) {
            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            shortToast("로그인 성공")
        }
    }

    private fun loginFailed(loginText: EditText) {
        loginText.background = ContextCompat.getDrawable(loginText.context, R.drawable.bac_edit_text_failed)
        loginText.requestFocus()
        loginText.selectAll()
    }
}