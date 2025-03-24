package com.api.palette.ui.main.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.api.palette.databinding.FragmentChangeBirthDateBinding
import com.api.palette.ui.util.shortToast
import com.api.palette.viewmodel.InfoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChangeBirthDateFragment : Fragment() {

    private lateinit var binding: FragmentChangeBirthDateBinding
    private val infoViewModel: InfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChangeBirthDateBinding.inflate(inflater, container, false)

        datePickerDefaultSettings()

        binding.ivArrowBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        binding.changeBirthDateBtn.setOnClickListener {
            val birthDate = getSelectedDate()
            if (birthDate.isEmpty()) {
                shortToast("생년월일을 설정해주세요.")
                return@setOnClickListener
            }
            infoViewModel.changeBirthDate(birthDate)
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun datePickerDefaultSettings() {
        val datePicker = binding.changeBirthDateSpinner
        datePicker.maxDate = System.currentTimeMillis() - 1000

        val calendar = Calendar.getInstance()
        calendar.set(2000, Calendar.JANUARY, 1)

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, _, _, _ -> }
    }

    private fun getSelectedDate(): String {
        val datePicker = binding.changeBirthDateSpinner
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year
        val calendar = Calendar.getInstance().apply { set(year, month, day) }
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return format.format(calendar.time)
    }
}
