package com.api.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import app.rive.runtime.kotlin.RiveAnimationView
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding3Binding

class Onboarding3Fragment : Fragment() {
    private lateinit var binding: FragmentOnboarding3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        val riveAnimationView: RiveAnimationView = binding.onBoarding3Animation
        riveAnimationView.setRiveResource(R.raw.swipe)

        binding.onBoarding3Button.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }
        return binding.root
    }
}