package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.R
import com.api.palette.databinding.FragmentChangeNameBinding
import com.api.palette.presentation.main.settings.editprofile.viewmodel.EditProfileViewModel
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNameFragment : Fragment() {

    private lateinit var binding: FragmentChangeNameBinding
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        setupEditTextFocus()
        setupBackButton()
        setupSubmitButton()
        return binding.root
    }

    private fun setupEditTextFocus() {
        binding.etChangeName.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            binding.etChangeName.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), color)
        }
    }

    private fun setupBackButton() {
        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSubmitButton() {
        binding.changeNameBtn.setOnClickListener {
            val username = binding.etChangeName.text.toString().trim()
            if (username.isEmpty()) {
                shortToast("이름을 입력해주세요.")
                return@setOnClickListener
            }

            editProfileViewModel.changeName(username) { result ->
                if (result.isSuccess) {
                    shortToast("프로필이 성공적으로 업데이트되었습니다.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("프로필 업데이트에 실패했습니다.")
                }
            }
        }
    }
}
