package com.api.palette.presentation.register

import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinBirthBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.fragment.app.viewModels

@AndroidEntryPoint
class JoinBirthFragment : Fragment() {
    private var _binding: FragmentJoinBirthBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels({ requireActivity() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinBirthBinding.inflate(inflater, container, false)
        datePickerDefaultSettings()

        binding.btnContinue.setOnClickListener {
            val dateOfBirth = getSelectedDate()
            registerViewModel.birthDate.value = dateOfBirth
            registerViewModel.register()

            registerViewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    findNavController().navigate(R.id.action_joinBirthFragment_to_joinCheckNumFragment)
                } else {
                    Toast.makeText(requireContext(), "회원가입 요청 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun datePickerDefaultSettings() {
        val datePicker: DatePicker = binding.dpSpinner
        datePicker.maxDate = System.currentTimeMillis() - 1000
        val calendar = Calendar.getInstance().apply { set(2000, Calendar.JANUARY, 1) }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, _, _, _ -> }
    }

    private fun getSelectedDate(): String {
        val datePicker: DatePicker = binding.dpSpinner
        val calendar = Calendar.getInstance().apply { set(datePicker.year, datePicker.month, datePicker.dayOfMonth) }
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
