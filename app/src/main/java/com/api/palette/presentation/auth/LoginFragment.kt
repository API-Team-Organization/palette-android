package com.api.palette.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentLoginBinding
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.findNavController

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        observeViewModel()
        return binding.root
    }

    private fun initView() {
        with(binding) {
            btnLogin.setOnClickListener {
                if (etLoginEmail.text.isNullOrEmpty()) {
                    etLoginEmail.error = "이메일을 입력해주세요."
                    return@setOnClickListener
                }
                if (etLoginPassword.text.isNullOrEmpty()) {
                    etLoginPassword.error = "비밀번호를 입력해주세요."
                    return@setOnClickListener
                }
                loginViewModel.login(etLoginEmail.text.toString(), etLoginPassword.text.toString())
            }

            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
            }
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginSuccess.observe(viewLifecycleOwner, Observer { token ->
            PaletteApplication.prefs.token = token
            startActivity(Intent(activity, ServiceActivity::class.java))
            activity?.finish()
        })
        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            shortToast(error)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
