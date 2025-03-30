package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.databinding.FragmentChangeBirthDateBinding
import com.api.palette.presentation.main.settings.editprofile.viewmodel.EditProfileViewModel
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ChangeBirthDateFragment : Fragment() {

    private var _binding: FragmentChangeBirthDateBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeBirthDateBinding.inflate(inflater, container, false)
        setupDatePicker()
        setupBackButton()
        return binding.root
    }

    private fun setupDatePicker() {
        val datePicker = binding.changeBirthDateSpinner
        datePicker.maxDate = System.currentTimeMillis() - 1000

        val defaultCalendar = Calendar.getInstance().apply {
            set(2000, Calendar.JANUARY, 1)
        }

        datePicker.init(
            defaultCalendar.get(Calendar.YEAR),
            defaultCalendar.get(Calendar.MONTH),
            defaultCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ -> }

        binding.changeBirthDateBtn.setOnClickListener {
            val birthDate = getSelectedDate()
            if (birthDate.isEmpty()) {
                shortToast("생년월일을 설정해주세요.")
                return@setOnClickListener
            }

            editProfileViewModel.changeBirthDate(birthDate) { result ->
                if (result.isSuccess) {
                    shortToast("프로필이 성공적으로 업데이트되었습니다.")
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("프로필 업데이트에 실패했습니다.")
                }
            }
        }
    }

    private fun setupBackButton() {
        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun getSelectedDate(): String {
        val datePicker = binding.changeBirthDateSpinner
        val calendar = Calendar.getInstance().apply {
            set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
