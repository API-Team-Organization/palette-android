package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinEmailBinding
import com.api.palette.application.PaletteApplication
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class JoinEmailFragment : Fragment() {
    private var _binding: FragmentJoinEmailBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinEmailBinding.inflate(inflater, container, false)
        binding.btnCheckNum.setOnClickListener { checkEmail() }
        binding.etJoinEmail.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinEmail.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        return binding.root
    }

    private fun checkEmail() {
        val emailInput = binding.etJoinEmail.text.toString().trim()
        if (emailInput.isEmpty() || !emailRegularExpression(emailInput)) {
            binding.etJoinEmail.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
            shortToast("올바른 이메일을 입력해주세요.")
            return
        }
        registerViewModel.email.value = emailInput
        PaletteApplication.prefs.userId = emailInput
        findNavController().navigate(R.id.action_joinEmailFragment_to_joinPasswordFragment)
    }

    private fun emailRegularExpression(email: String): Boolean {
        val emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}\$"
        return Pattern.compile(emailPattern).matcher(email).find()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
