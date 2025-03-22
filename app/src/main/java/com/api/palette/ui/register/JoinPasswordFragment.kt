package com.api.palette.ui.register

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinPasswordBinding
import com.api.palette.viewmodel.RegisterViewModel
import java.util.regex.Pattern

class JoinPasswordFragment : Fragment() {
    private lateinit var binding: FragmentJoinPasswordBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)
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
        val password = binding.etPassword.text.toString()
        val checkedPassword = binding.etCheckPassword.text.toString()
        if (password.isEmpty()) {
            checkPasswordFailed(binding.etPassword)
            binding.failedPasswordEmpty.visibility = View.VISIBLE
            binding.failedCheckPasswordEmpty.visibility = View.GONE
            binding.failedCheckPasswordDiff.visibility = View.GONE
            binding.failedPasswordFormat.visibility = View.GONE
        } else if (checkedPassword.isEmpty()) {
            checkPasswordFailed(binding.etCheckPassword)
            binding.failedCheckPasswordEmpty.visibility = View.VISIBLE
            binding.failedPasswordEmpty.visibility = View.GONE
            binding.failedCheckPasswordDiff.visibility = View.GONE
            binding.failedPasswordFormat.visibility = View.GONE
        } else if (password != checkedPassword) {
            checkPasswordFailed(binding.etCheckPassword)
            binding.failedCheckPasswordDiff.visibility = View.VISIBLE
            binding.failedPasswordEmpty.visibility = View.GONE
            binding.failedCheckPasswordEmpty.visibility = View.GONE
            binding.failedPasswordFormat.visibility = View.GONE
        } else {
            val isPasswordValid = passwordRegularExpression(password)
            if (isPasswordValid) {
                registerViewModel.setPassword(checkedPassword)
                findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
            } else {
                checkPasswordFailed(binding.etPassword)
                binding.failedPasswordFormat.visibility = View.VISIBLE
                binding.failedPasswordEmpty.visibility = View.GONE
                binding.failedCheckPasswordEmpty.visibility = View.GONE
                binding.failedCheckPasswordDiff.visibility = View.GONE
            }
        }
    }

    private fun checkPasswordFailed(password: EditText) {
        password.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        password.requestFocus()
        password.selectAll()
    }

    private fun passwordRegularExpression(password: String): Boolean {
        val passwordPattern = "^.*(?=^.{8,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }
}
