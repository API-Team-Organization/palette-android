package com.example.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private lateinit var binding : FragmentStartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }

        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_joinEmailFragment)
        }

        return binding.root
    }
}