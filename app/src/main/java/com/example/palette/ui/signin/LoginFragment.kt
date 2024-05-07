package com.example.palette.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        with(binding) {
            loginButton.setOnClickListener {
                if (loginEmailEdit.text.isNullOrBlank()) {
                    loginFailed(loginEmailEdit, 0)
                    passwordFailedText.visibility = View.GONE
                    loginPasswordEdit.setBackgroundDrawable(ContextCompat.getDrawable(loginPasswordEdit.context, R.drawable.bac_edit_text))
                }
                else if (loginPasswordEdit.text.isNullOrBlank()) {
                    loginFailed(loginPasswordEdit, 1)
                    emailFailedText.visibility = View.GONE
                    loginEmailEdit.setBackgroundDrawable(ContextCompat.getDrawable(loginEmailEdit.context, R.drawable.bac_edit_text))

                }
                else {
                    login()
                }
            }

        }

        return binding.root
    }


    private fun login() {
        binding.emailFailedText.visibility = View.GONE
        binding.loginEmailEdit.setBackgroundDrawable(ContextCompat.getDrawable(binding.loginEmailEdit.context, R.drawable.bac_edit_text))
        binding.passwordFailedText.visibility = View.GONE
        binding.loginPasswordEdit.setBackgroundDrawable(ContextCompat.getDrawable(binding.loginPasswordEdit.context, R.drawable.bac_edit_text))

        // 서버랑 통신해서 이메일 존재여부, 비밀번호 일치 등 확인 후, 로그인 성공 Toast
        if (true) {
            findNavController().navigate(R.id.action_loginFragment_to_MainFragment)
            Toast.makeText(activity, "로그인 성공", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginFailed(loginText: EditText, flag : Int) {
        if (flag == 0) {
            binding.emailFailedText.visibility = View.VISIBLE
        }
        else {
            binding.passwordFailedText.visibility = View.VISIBLE
        }
        loginText.setBackgroundDrawable(ContextCompat.getDrawable(loginText.context, R.drawable.bac_edit_text_failed))
        loginText.requestFocus()
        loginText.selectAll()
    }

}