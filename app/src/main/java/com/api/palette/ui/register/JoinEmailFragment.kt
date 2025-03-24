package com.api.palette.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentJoinEmailBinding
import com.api.palette.viewmodel.RegisterViewModel
import java.util.regex.Pattern

class JoinEmailFragment : Fragment() {
    private lateinit var binding: FragmentJoinEmailBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentJoinEmailBinding.inflate(inflater, container, false)
        binding.btnCheckNum.setOnClickListener { checkEmail() }
        binding.etJoinEmail.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinEmail.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }

        return binding.root
    }

    private fun checkEmail() {
        val email = binding.etJoinEmail.text.toString().trim()

        if (email.isEmpty()) {
            checkEmailFailed(binding.etJoinEmail)
            binding.failedEmailEmpty.visibility = View.VISIBLE
            binding.failedEmailFormat.visibility = View.GONE
        } else {
            val isEmailValid = emailRegularExpression(email)
            if (isEmailValid) {
                registerViewModel.setEmail(email)
                PaletteApplication.prefs.userId = email
                findNavController().navigate(R.id.action_joinEmailFragment_to_joinPasswordFragment)
            } else {
                checkEmailFailed(binding.etJoinEmail)
                binding.failedEmailFormat.visibility = View.VISIBLE
                binding.failedEmailEmpty.visibility = View.GONE
            }
        }
    }

    private fun checkEmailFailed(email: EditText) {
        email.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        email.requestFocus()
        email.selectAll()
    }

    private fun emailRegularExpression(email: String): Boolean {
        val emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}\$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.find()
    }
}
