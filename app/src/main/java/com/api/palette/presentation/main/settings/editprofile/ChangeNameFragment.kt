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
import com.api.palette.databinding.FragmentChangeNameBinding
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException

@AndroidEntryPoint
class ChangeNameFragment : Fragment() {

    private lateinit var binding: FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)

        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.GONE

        binding.etChangeName.setOnFocusChangeListener { _, hasFocus ->
            val color = if (hasFocus) R.color.blue else R.color.black
            binding.etChangeName.backgroundTintList = ContextCompat.getColorStateList(requireContext(), color)
        }

        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeNameBtn.setOnClickListener {
            val username = binding.etChangeName.text.toString().trim()

            if (username.isEmpty()) {
                shortToast("이름을 입력해주세요.")
                return@setOnClickListener
            }

            PaletteApplication.prefs.username = ""
            updateName(username)
        }
    }

    private fun updateName(username: String) {
        lifecycleScope.launch {
            try {
                val response = com.api.palette.data.info.InfoRequestManager
                    .changeNameRequest(PaletteApplication.prefs.token, username)

                if (response.isSuccessful) {
                    shortToast("프로필이 성공적으로 업데이트되었습니다.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("프로필 업데이트에 실패했습니다.")
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
