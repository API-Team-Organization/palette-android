package com.api.palette.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        PaletteApplication.prefs.isFirst = false
        if (!PaletteApplication.prefs.isFirst) {
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
