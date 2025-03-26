package com.api.palette.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentJoinBirthBinding
import com.api.palette.presentation.register.viewmodel.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterBirthFragment : Fragment() {

    private var _binding: FragmentJoinBirthBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinBirthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDatePicker()
        setupContinueButton()
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance().apply {
            set(2000, Calendar.JANUARY, 1)
        }

        binding.dpSpinner.apply {
            maxDate = System.currentTimeMillis() - 1000
            init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ) { _, _, _, _ ->
                registerViewModel.setBirthdate(getSelectedDate())
            }
        }
    }

    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener {
            registerViewModel.setBirthdate(getSelectedDate())
            findNavController().navigate(R.id.action_joinBirthFragment_to_joinNameFragment)
        }
    }

    private fun getSelectedDate(): String {
        val year = binding.dpSpinner.year
        val month = binding.dpSpinner.month
        val day = binding.dpSpinner.dayOfMonth
        val calendar = Calendar.getInstance().apply { set(year, month, day) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
