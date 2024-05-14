package com.example.palette.ui.signup

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinBirthBinding
import com.example.palette.databinding.FragmentJoinPasswordBinding
import java.util.Calendar

class JoinBirthFragment : Fragment() {
    private lateinit var binding : FragmentJoinBirthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinBirthBinding.inflate(inflater, container, false)

        datePickerDefaultSettings()

        binding.btnContinue.setOnClickListener {
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
        }
    }
}