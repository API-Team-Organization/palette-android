package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.auth.AuthRequestManager
import com.api.palette.databinding.FragmentChangePasswordBinding
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.GONE

        initView()

        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    private fun initView() {
        binding.etBeforePassword.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            binding.etBeforePassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
        }

        binding.etAfterPassword.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            binding.etAfterPassword.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePasswordBtn.setOnClickListener {
            val beforePassword = binding.etBeforePassword.text.toString().trim()
            val afterPassword = binding.etAfterPassword.text.toString().trim()

            if (beforePassword.isEmpty() || afterPassword.isEmpty()) {
                shortToast("이전 비밀번호와 변경할 비밀번호를 입력해주세요.")
                return@setOnClickListener
            }

            changePassword(beforePassword, afterPassword)
        }
    }

    private fun changePassword(beforePassword: String, afterPassword: String) {
        lifecycleScope.launch {
            try {
                val response = AuthRequestManager.changePasswordRequest(
                    PaletteApplication.prefs.token, beforePassword, afterPassword
                )
                if (response.isSuccessful) {
                    shortToast("비밀번호가 성공적으로 변경되었습니다.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("비밀번호 변경에 실패했습니다.")
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다.")
            } catch (e: Exception) {
                shortToast("네트워크 오류가 발생했습니다.")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.VISIBLE
    }
}
