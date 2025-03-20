package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import app.rive.runtime.kotlin.RiveAnimationView
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding3Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Onboarding3Fragment : Fragment() {
    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        val riveAnimationView: RiveAnimationView = binding.onBoarding3Animation
        riveAnimationView.setRiveResource(R.raw.swipe)
        binding.onBoarding3Button.setOnClickListener {
            findNavController().navigate(R.id.action_onboarding3Fragment_to_startFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
