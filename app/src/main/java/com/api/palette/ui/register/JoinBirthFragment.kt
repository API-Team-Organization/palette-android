package com.api.palette.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinBirthBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class JoinBirthFragment : Fragment() {
    private lateinit var binding: FragmentJoinBirthBinding
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentJoinBirthBinding.inflate(inflater, container, false)

        datePickerDefaultSettings()

        binding.btnContinue.setOnClickListener {
            val dateOfBirth = getSelectedDate()

            registerViewModel.birthdate.observe(viewLifecycleOwner) {
                getSelectedDate()
            }
            registerViewModel.setBirthdate(dateOfBirth)

            findNavController().navigate(R.id.action_joinBirthFragment_to_joinNameFragment)
        }

        return binding.root
    }

    private fun datePickerDefaultSettings() {
        val datePicker = binding.dpSpinner
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
        val datePicker = binding.dpSpinner
        val day = datePicker.dayOfMonth
        val month = datePicker.month
        val year = datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }
}
