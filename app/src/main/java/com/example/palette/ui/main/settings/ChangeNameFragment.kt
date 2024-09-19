package com.example.palette.ui.main.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentChangeNameBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChangeNameFragment : Fragment() {

    private lateinit var binding: FragmentChangeNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)

        (requireActivity() as BaseControllable).bottomVisible(false)

        binding.etChangeName.setOnFocusChangeListener { _, hasFocus ->
            binding.etChangeName.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    if (hasFocus) R.color.blue else R.color.black
                )
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
            changeName(username)
        }
    }

    private fun changeName(username: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = InfoRequestManager.changeNameRequest(
                    PaletteApplication.prefs.token,
                    username,
                    null
                )

                if (response.isSuccessful) {
                    // 프로필 업데이트 성공
                    shortToast("프로필이 성공적으로 업데이트되었습니다.")
                    Log.d("ProfileEditFragment", "Profile updated successfully.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    // 프로필 업데이트 실패
                    shortToast("프로필 업데이트에 실패했습니다.")
                    Log.e(
                        "ProfileEditFragment",
                        "Failed to update profile: ${response.code()} - ${response.message()}"
                    )
                    // 실패 시 처리 코드 추가
                }
            } catch (e: HttpException) {
                shortToast("서버 오류가 발생했습니다.")
                Log.e("ProfileEditFragment", "Server error", e)
                // HttpException 발생 시 처리 코드 추가
            } catch (e: Exception) {
                shortToast("네트워크 오류가 발생했습니다.")
                Log.e("ProfileEditFragment", "Network error", e)
                // 기타 예외 발생 시 처리 코드 추가
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}