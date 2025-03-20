package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentOnboardingDefaultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingDefaultFragment : Fragment() {
    private var _binding: FragmentOnboardingDefaultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboardingDefaultBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        if (!PaletteApplication.prefs.isFirst) {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }
        binding.registerViewpager.adapter = RegisterPagerAdapter(childFragmentManager)
        binding.registerViewpager.offscreenPageLimit = 2
        binding.dotsIndicator.attachTo(binding.registerViewpager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
