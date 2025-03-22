package com.api.palette.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentLoginBinding
import com.api.palette.ui.main.ServiceActivity
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private var backPressedTime: Long = 0L
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        handleOnBackPressed()
        changeEditTextFocusColor(binding.etLoginEmail)
        changeEditTextFocusColor(binding.etLoginPassword)
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        loginViewModel.loginResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null) {
                val intent = Intent(activity, ServiceActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }
        })

        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                shortToast(error)
            }
        })
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener {
            if (binding.etLoginEmail.text.isNullOrEmpty()) {
                handleLoginFailure(binding.etLoginEmail)
                binding.emailFailedText.visibility = View.VISIBLE
                binding.passwordFailedText.visibility = View.GONE
                binding.etLoginPassword.background = ContextCompat.getDrawable(binding.etLoginPassword.context, R.drawable.bac_card_background)
                return@setOnClickListener
            }
            if (binding.etLoginPassword.text.isNullOrEmpty()) {
                handleLoginFailure(binding.etLoginPassword)
                binding.passwordFailedText.visibility = View.VISIBLE
                binding.emailFailedText.visibility = View.GONE
                binding.etLoginEmail.background = ContextCompat.getDrawable(binding.etLoginEmail.context, R.drawable.bac_card_background)
                return@setOnClickListener
            }
            val email = binding.etLoginEmail.text.toString()
            val pw = binding.etLoginPassword.text.toString()
            loginViewModel.login(email, pw)
        }
        binding.tvRegister.setOnClickListener {
            disableOnBackPressedCallback()
            findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
        }
    }

    private fun changeEditTextFocusColor(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                editText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }
    }

    private fun handleLoginFailure(editText: EditText) {
        editText.background = ContextCompat.getDrawable(editText.context, R.drawable.bac_edit_text_error)
        editText.requestFocus()
        editText.selectAll()
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

    private fun disableOnBackPressedCallback() {
        callback.isEnabled = false
    }
}
