package com.example.palette.ui.signup

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinEmailBinding
import com.example.palette.databinding.FragmentJoinPasswordBinding
import java.util.regex.Pattern

class JoinPasswordFragment : Fragment() {
    private lateinit var binding : FragmentJoinPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinPasswordBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val password = binding.etPassword.text.toString()
            val checkPassword = binding.etCheckPassword.text.toString()

            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호 값이 비어있습니다", Toast.LENGTH_SHORT).show()
            } else if (checkPassword.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호 확인 값이 비어있습니다", Toast.LENGTH_SHORT).show()
            } else if (password!=checkPassword){
                Toast.makeText(requireContext(), "비밀번호 확인 값이 다릅니다.", Toast.LENGTH_SHORT).show()
            } else {
                val isPasswordValid = checkPasswordFormat(password)
                Log.d("isPasswordValid", "${checkPasswordFormat(password)}")

                if (isPasswordValid) {
                    findNavController().navigate(R.id.action_joinPasswordFragment_to_joinBirthFragment)
                } else {
                    Toast.makeText(requireContext(), "비밀번호 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun checkPasswordFormat(password: String): Boolean {
        val passwordPattern = "^.*(?=^.{8,15}\$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#\$%^&+=]).*\$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }
}