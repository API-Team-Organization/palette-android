package com.api.palette.presentation.login

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.common.Constant
import com.api.palette.data.error.CustomException
import com.api.palette.databinding.FragmentLoginBinding
import com.api.palette.presentation.login.viewmodel.LoginViewModel
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var callback: OnBackPressedCallback
    private var backPressedTime: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        initView()
        handleOnBackPressed()
        changeEditTextFocusColor(binding.etLoginEmail)
        changeEditTextFocusColor(binding.etLoginPassword)
        return binding.root
    }

    private fun initView() {
        binding.btnLogin.setOnClickListener {
            when {
                binding.etLoginEmail.text.isNullOrEmpty() -> {
                    handleLoginFailure(binding.etLoginEmail)
                    binding.emailFailedText.visibility = View.VISIBLE
                    binding.passwordFailedText.visibility = View.GONE
                    binding.etLoginPassword.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bac_card_background)
                }
                binding.etLoginPassword.text.isNullOrEmpty() -> {
                    handleLoginFailure(binding.etLoginPassword)
                    binding.passwordFailedText.visibility = View.VISIBLE
                    binding.emailFailedText.visibility = View.GONE
                    binding.etLoginEmail.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bac_card_background)
                }
                else -> loginRequest()
            }
        }
        binding.tvRegister.setOnClickListener {
            disableOnBackPressedCallback()
            findNavController().navigate(R.id.action_loginFragment_to_joinEmailFragment)
        }
    }

    private fun changeEditTextFocusColor(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            editText.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (hasFocus) R.color.blue else R.color.black
            )
        }
    }

    private fun handleLoginFailure(editText: EditText) {
        editText.apply {
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bac_edit_text_error)
            requestFocus()
            selectAll()
        }
    }

    private fun loginRequest() {
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.login(email, password) { result ->
                result.fold(
                    onSuccess = { token ->
                        Log.d(Constant.TAG, "token is $token")
                        PaletteApplication.prefs.token = token
                        startActivity(Intent(requireActivity(), ServiceActivity::class.java))
                        requireActivity().finish()
                    },
                    onFailure = { e ->
                        when (e) {
                            is CustomException -> shortToast(e.errorResponse.message)
                            else -> {
                                Log.e("LoginFragment", "loginRequest error", e)
                                shortToast("오류가 발생했습니다.")
                            }
                        }
                    }
                )
            }
        }
    }

    private fun handleOnBackPressed() {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime <= 2000) {
                    requireActivity().finish()
                } else {
                    backPressedTime = System.currentTimeMillis()
                    shortToast("한 번 더 누르면 종료됩니다.")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun disableOnBackPressedCallback() {
        callback.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        callback.remove()
    }
}
