package com.api.palette.presentation.onboarding

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

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleFirstLaunch()
        initClickListeners()
    }

    private fun handleFirstLaunch() {
        val prefs = PaletteApplication.prefs
        if (prefs.isFirst) {
            prefs.isFirst = false
        } else {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
    }

    private fun initClickListeners() {
        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_joinEmailFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
