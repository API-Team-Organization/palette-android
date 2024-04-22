package com.example.palette.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.databinding.FragmentOnboarding1Binding
import com.example.palette.databinding.FragmentOnboardingDefaultBinding

class OnboardingDefaultFragment : Fragment() {
    private val binding by lazy { FragmentOnboardingDefaultBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding.registerViewpager.adapter = RegisterPagerAdapter(requireActivity().supportFragmentManager, binding)
        binding.registerViewpager.offscreenPageLimit = 2
        binding.dotsIndicator.setViewPager(binding.registerViewpager)


        binding.onBoardingButton.setOnClickListener {

            when(binding.registerViewpager.currentItem) {
                0 -> {
                    binding.registerViewpager.setCurrentItem(binding.registerViewpager.currentItem+1, true)
                }
                1 -> {
                    binding.registerViewpager.setCurrentItem(binding.registerViewpager.currentItem+1, true)
                }
                2 -> {
                    findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
                }
            }
        }

        return binding.root
    }
}