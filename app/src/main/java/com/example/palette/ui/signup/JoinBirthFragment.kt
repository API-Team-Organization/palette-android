package com.example.palette.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinBirthBinding
import com.example.palette.databinding.FragmentJoinPasswordBinding

class JoinBirthFragment : Fragment() {
    private lateinit var binding : FragmentJoinBirthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinBirthBinding.inflate(inflater, container, false)

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_joinBirthFragment_to_joinNameFragment)
        }

        return binding.root
    }
}