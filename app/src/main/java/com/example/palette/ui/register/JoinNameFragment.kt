package com.example.palette.ui.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinNameBinding
import java.util.regex.Pattern

class JoinNameFragment : Fragment() {
    private lateinit var binding : FragmentJoinNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinNameBinding.inflate(inflater, container, false)

        binding.btnComplete.setOnClickListener {
            val name = binding.etJoinName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이름 값이 비어있습니다", Toast.LENGTH_SHORT).show()
            } else {
                val isNameValid = checkNameFormat(name)
                Log.d("isNameValid", "${checkNameFormat(name)}")

                if (isNameValid) {
                    findNavController().navigate(R.id.action_joinNameFragment_to_joinCompleteFragment)
                } else {
                    Toast.makeText(requireContext(), "이름의 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun checkNameFormat(name: String): Boolean {
        val namePattern = "^[가-힣]*\$"
        val pattern = Pattern.compile(namePattern)
        val matcher = pattern.matcher(name)
        return matcher.find()
    }
}