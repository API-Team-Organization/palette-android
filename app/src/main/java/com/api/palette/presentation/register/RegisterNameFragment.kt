package com.api.palette.presentation.register

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentJoinNameBinding
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import com.api.palette.presentation.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

class RegisterNameFragment : Fragment() {
    private var _binding: FragmentJoinNameBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnComplete.setOnClickListener { validateName() }
        binding.etJoinName.setOnFocusChangeListener { _, hasFocus ->
            binding.etJoinName.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                if (hasFocus) R.color.blue else R.color.black
            )
        }
    }

    private fun validateName() {
        val name = binding.etJoinName.text.toString()
        when {
            name.isBlank() -> showNameError(isFormatError = false)
            !isValidKoreanName(name) -> showNameError(isFormatError = true)
            else -> {
                registerViewModel.setUsername(name)
                registerUser()
            }
        }
    }

    private fun showNameError(isFormatError: Boolean) = with(binding) {
        etJoinName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
        etJoinName.requestFocus()
        etJoinName.selectAll()

        failedNameEmpty.visibility = if (isFormatError) View.GONE else View.VISIBLE
        failedNameFormat.visibility = if (isFormatError) View.VISIBLE else View.GONE
    }

    private fun registerUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            registerViewModel.register { result ->
                result.fold(
                    onSuccess = { token ->
                        PaletteApplication.prefs.token = token
                        shortToast("이메일 인증을 진행해주세요.")
                        findNavController().navigate(R.id.action_joinNameFragment_to_joinCheckNumFragment)
                    },
                    onFailure = { e ->
                        when (e) {
                            is SocketTimeoutException -> shortToast("요청 시간 초과")
                            is HttpException -> shortToast("서버 오류 발생: ${e.code()}")
                            else -> shortToast("예기치 않은 오류 발생: ${e.message}")
                        }
                    }
                )
            }
        }
    }

    private fun isValidKoreanName(name: String): Boolean {
        return Pattern.matches("^[가-힣]+$", name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
