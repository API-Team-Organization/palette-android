package com.api.palette.presentation.register

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinPasswordBinding
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import java.util.regex.Pattern

class RegisterPasswordFragment : Fragment() {
    private var _binding: FragmentJoinPasswordBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnNext.setOnClickListener { validatePassword() }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            setEditTextFocusColor(binding.etPassword, hasFocus)
        }
        binding.etCheckPassword.setOnFocusChangeListener { _, hasFocus ->
            setEditTextFocusColor(binding.etCheckPassword, hasFocus)
        }
    }

    private fun validatePassword() {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etCheckPassword.text.toString()
        when {
            password.isEmpty() -> {
                showError(binding.etPassword, showEmpty = true)
            }
            confirmPassword.isEmpty() -> {
                showError(binding.etCheckPassword, checkEmpty = true)
            }
            password != confirmPassword -> {
                showError(binding.etCheckPassword, mismatch = true)
            }
            !isValidPassword(password) -> {
                showError(binding.etPassword, invalidFormat = true)
            }
            else -> {
                registerViewModel.setPassword(password)
                findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
            }
        }
    }

    private fun showError(
        target: EditText,
        showEmpty: Boolean = false,
        checkEmpty: Boolean = false,
        mismatch: Boolean = false,
        invalidFormat: Boolean = false
    ) {
        target.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        target.requestFocus()
        target.selectAll()

        binding.failedPasswordEmpty.visibility = if (showEmpty) View.VISIBLE else View.GONE
        binding.failedCheckPasswordEmpty.visibility = if (checkEmpty) View.VISIBLE else View.GONE
        binding.failedCheckPasswordDiff.visibility = if (mismatch) View.VISIBLE else View.GONE
        binding.failedPasswordFormat.visibility = if (invalidFormat) View.VISIBLE else View.GONE
    }

    private fun setEditTextFocusColor(editText: EditText, hasFocus: Boolean) {
        val colorRes = if (hasFocus) R.color.blue else R.color.black
        editText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = "^.*(?=^.{8,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$"
        return Pattern.matches(regex, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
