package com.example.palette.ui.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentJoinBirthBinding
import com.example.palette.databinding.FragmentJoinNameBinding

class JoinNameFragment : Fragment() {
    private lateinit var binding : FragmentJoinNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentJoinNameBinding.inflate(inflater, container, false)

        binding.btnComplete.setOnClickListener {
            findNavController().navigate(R.id.action_joinNameFragment_to_joinCompleteFragment)
        }

        return binding.root
    }
}