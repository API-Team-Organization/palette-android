package com.api.palette.presentation.register

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentJoinEmailBinding
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import java.util.regex.Pattern

class RegisterEmailFragment : Fragment() {

    private var _binding: FragmentJoinEmailBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
    }

    private fun initListeners() = with(binding) {
        btnCheckNum.setOnClickListener { validateEmail() }
        etJoinEmail.setOnFocusChangeListener { _, hasFocus ->
            etJoinEmail.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (hasFocus) R.color.blue else R.color.black
            )
        }
    }

    private fun validateEmail() {
        val email = binding.etJoinEmail.text.toString().trim()

        when {
            email.isEmpty() -> showEmailError(isFormatError = false)
            !isValidEmail(email) -> showEmailError(isFormatError = true)
            else -> {
                registerViewModel.setEmail(email)
                PaletteApplication.prefs.userId = email
                findNavController().navigate(R.id.action_joinEmailFragment_to_joinPasswordFragment)
            }
        }
    }

    private fun showEmailError(isFormatError: Boolean) = with(binding) {
        etJoinEmail.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        etJoinEmail.requestFocus()
        etJoinEmail.selectAll()

        failedEmailEmpty.visibility = if (isFormatError) View.GONE else View.VISIBLE
        failedEmailFormat.visibility = if (isFormatError) View.VISIBLE else View.GONE
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$"
        return Pattern.compile(regex).matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
