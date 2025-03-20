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
import com.api.palette.databinding.FragmentJoinNameBinding
import com.api.palette.ui.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class JoinNameFragment : Fragment() {
    private var _binding: FragmentJoinNameBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinNameBinding.inflate(inflater, container, false)
        binding.btnComplete.setOnClickListener { checkName() }
        binding.etJoinName.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), if (hasFocus) R.color.blue else R.color.black)
        }
        return binding.root
    }

    private fun checkName() {
        val name = binding.etJoinName.text.toString().trim()
        if (name.isEmpty() || !nameRegularExpression(name)) {
            checkNameFailed(binding.etJoinName)
            shortToast("올바른 이름을 입력해주세요.")
            return
        }
        registerViewModel.username.value = name
        findNavController().navigate(R.id.action_joinNameFragment_to_joinCompleteFragment)
    }

    private fun checkNameFailed(editText: EditText) {
        editText.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        editText.requestFocus()
        editText.selectAll()
    }

    private fun nameRegularExpression(name: String): Boolean {
        val namePattern = "^[가-힣]+\$"
        return Pattern.compile(namePattern).matcher(name).find()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
