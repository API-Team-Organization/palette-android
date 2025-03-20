package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinPasswordBinding
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class JoinPasswordFragment : Fragment() {
    private var _binding: FragmentJoinPasswordBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)
        binding.btnNext.setOnClickListener { checkPassword() }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            binding.etPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        binding.etCheckPassword.setOnFocusChangeListener { _, hasFocus ->
            binding.etCheckPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        return binding.root
    }

    private fun checkPassword() {
        val password = binding.etPassword.text.toString().trim()
        val checkPassword = binding.etCheckPassword.text.toString().trim()
        if (password.isEmpty() || checkPassword.isEmpty() || password != checkPassword || !passwordRegularExpression(password)) {
            checkPasswordFailed(binding.etPassword)
            shortToast("비밀번호를 확인해주세요.")
            return
        }
        registerViewModel.password.value = password
        findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
    }

    private fun checkPasswordFailed(editText: EditText) {
        editText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        editText.requestFocus()
        editText.selectAll()
    }

    private fun passwordRegularExpression(password: String): Boolean {
        val passwordPattern = "^.*(?=^.{8,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$"
        return Pattern.compile(passwordPattern).matcher(password).find()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
