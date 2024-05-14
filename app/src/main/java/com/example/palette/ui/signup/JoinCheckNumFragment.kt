package com.example.palette.ui.signup

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinCheckNumBinding
import com.example.palette.databinding.FragmentJoinEmailBinding
import com.example.palette.databinding.FragmentStartBinding
import kotlin.math.log

class JoinCheckNumFragment : Fragment() {
    private lateinit var binding : FragmentJoinCheckNumBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinCheckNumBinding.inflate(inflater, container, false)

        showEmail()

        binding.btnCheckNum.setOnClickListener {
            if (true) { // 인증번호가 맞다면
                findNavController().navigate(R.id.action_joinCheckNumFragment_to_joinPasswordFragment)
            }

        }

        return binding.root
    }

    private fun showEmail() {
        var result = arguments?.getString("email")

        result?.let {
            binding.nEtJoinEmail.apply {
                text = Editable.Factory.getInstance().newEditable(it)
                isEnabled = false
            }
        }
    }
}