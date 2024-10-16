package com.api.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private lateinit var binding : FragmentStartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentStartBinding.inflate(inflater, container, false)
        initView()

        return binding.root
    }

    private fun initView() {
        val prefs = PaletteApplication.prefs
        prefs.isFirst = false

        val isFirst = prefs.isFirst

        if (!isFirst) {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_joinEmailFragment)
        }
    }
}