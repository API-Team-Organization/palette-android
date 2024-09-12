package com.example.palette.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinPasswordBinding
import java.util.regex.Pattern

class JoinPasswordFragment : Fragment() {
    private lateinit var binding : FragmentJoinPasswordBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            checkPassword()
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                binding.etPassword.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        binding.etCheckPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.etCheckPassword.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            } else {
                binding.etCheckPassword.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.black)
            }
        }

        return binding.root
    }

    private fun checkPassword() {
        with(binding) {
            val password = binding.etPassword.text.toString()
            val checkedPassword = binding.etCheckPassword.text.toString()

            if (password.isEmpty()) {
                checkPasswordFailed(etPassword)
                failedPasswordEmpty.visibility = View.VISIBLE
                failedCheckPasswordEmpty.visibility = View.GONE
                failedCheckPasswordDiff.visibility = View.GONE
                failedPasswordFormat.visibility = View.GONE
                etCheckPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            } else if (checkedPassword.isEmpty()) {
                checkPasswordFailed(etCheckPassword)
                failedCheckPasswordEmpty.visibility = View.VISIBLE
                failedPasswordEmpty.visibility = View.GONE
                failedCheckPasswordDiff.visibility = View.GONE
                failedPasswordFormat.visibility = View.GONE
                etPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            } else if (password!=checkedPassword){
                checkPasswordFailed(etCheckPassword)
                failedCheckPasswordDiff.visibility = View.VISIBLE
                failedPasswordEmpty.visibility = View.GONE
                failedCheckPasswordEmpty.visibility = View.GONE
                failedPasswordFormat.visibility = View.GONE
                etPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            } else {
                val isPasswordValid = passwordRegularExpression(password)
                Log.d("isPasswordValid", "${passwordRegularExpression(password)}")

                if (isPasswordValid) {
                    registerViewModel.password.observe(viewLifecycleOwner) {
                        binding.etCheckPassword.setText(
                            it
                        )
                    }
                    registerViewModel.setPassword(binding.etCheckPassword.text.toString())

                    findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
                } else {
                    checkPasswordFailed(etPassword)
                    failedPasswordFormat.visibility = View.VISIBLE
                    failedPasswordEmpty.visibility = View.GONE
                    failedCheckPasswordEmpty.visibility = View.GONE
                    failedCheckPasswordDiff.visibility = View.GONE
                    etCheckPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                }
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