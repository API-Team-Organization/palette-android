package com.example.palette.ui.main.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.data.info.InfoRequestManager
import com.example.palette.databinding.FragmentChangeBirthDateBinding
import com.example.palette.ui.base.BaseControllable
import com.example.palette.ui.main.ServiceActivity
import com.example.palette.ui.register.RegisterViewModel
import com.example.palette.ui.util.shortToast
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChangeBirthDateFragment : Fragment() {

    private lateinit var binding: FragmentChangeBirthDateBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeBirthDateBinding.inflate(inflater, container, false)

        (requireActivity() as? BaseControllable)?.bottomVisible(false)
        datePickerDefaultSettings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changeBirthDateBtn.setOnClickListener {
            val birthDate = getSelectedDate()

            if (birthDate.isEmpty()) {
                shortToast("생년월일을 설정해주세요.")
                return@setOnClickListener
            }

            PaletteApplication.prefs.clearUser()
            changeBirthDate(birthDate)
        }
    }

    private fun changeBirthDate(birthDate: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = InfoRequestManager.changeBirthDateRequest(
                    PaletteApplication.prefs.token,
                    null,
                    birthDate
                )

                if (response.isSuccessful) {
                    // 프로필 업데이트 성공
                    shortToast("프로필이 성공적으로 업데이트되었습니다.")
                    Log.d("ProfileEditFragment", "Profile updated successfully.")
                    // 업데이트 성공 후 필요한 작업 처리
                } else {
                    // 프로필 업데이트 실패
                    shortToast("프로필 업데이트에 실패했습니다.")
                    Log.e("ProfileEditFragment", "Failed to update profile: ${response.code()} - ${response.message()}")
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

    private fun datePickerDefaultSettings() {
        val datePicker = binding.changeBirthDateSpinner
        datePicker.maxDate = System.currentTimeMillis() - 1000

        val calendar = Calendar.getInstance()
        calendar.set(2000, Calendar.JANUARY, 1)

        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { view, year, monthOfYear, dayOfMonth ->
            // 날짜가 변경될 때 실행할 작업
            val dateOfBirth = getSelectedDate()
            registerViewModel.setBirthdate(dateOfBirth)
        }
    }

    private fun getSelectedDate(): String {
        val datePicker = binding.changeBirthDateSpinner
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance().apply {
            set(year, month, day)
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}