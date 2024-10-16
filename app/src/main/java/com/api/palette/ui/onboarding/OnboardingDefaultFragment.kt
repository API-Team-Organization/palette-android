package com.api.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentOnboardingDefaultBinding

class OnboardingDefaultFragment : Fragment() {
    private val binding by lazy { FragmentOnboardingDefaultBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return binding.root
    }

    private fun initView() {
        val isFirst = PaletteApplication.prefs.isFirst

        if (!isFirst) {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }

        binding.registerViewpager.adapter =
            RegisterPagerAdapter(requireActivity().supportFragmentManager)
        binding.registerViewpager.offscreenPageLimit = 2
        binding.dotsIndicator.attachTo(binding.registerViewpager)
    }
}