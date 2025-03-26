package com.api.palette.presentation.main.settings.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.data.info.InfoRequestManager
import com.api.palette.databinding.FragmentChangeBirthDateBinding
import com.api.palette.presentation.base.BaseControllable
import com.api.palette.presentation.main.ServiceActivity
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import com.api.palette.presentation.util.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ChangeBirthDateFragment : Fragment() {

    private var _binding: FragmentChangeBirthDateBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeBirthDateBinding.inflate(inflater, container, false)

        (requireActivity() as? BaseControllable)?.bottomVisible(false)
        setupDatePicker()

        binding.ivArrowBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

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
            updateBirthDate(birthDate)
        }
    }

    private fun updateBirthDate(birthDate: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = InfoRequestManager.changeBirthDateRequest(
                    PaletteApplication.prefs.token, birthDate
                )
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
        ) { _, year, month, day ->
            val selectedDate = getSelectedDate()
            registerViewModel.setBirthdate(selectedDate)
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

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
