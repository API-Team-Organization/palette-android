package com.example.palette.ui.onboarding

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.palette.R
import com.example.palette.common.Constant
import com.example.palette.databinding.FragmentOnboardingDefaultBinding

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
        val pref = requireActivity().getSharedPreferences("notFirst", MODE_PRIVATE)
        val notFirst = pref.getBoolean("notFirst", true)

        if (notFirst) {
            Log.d(Constant.TAG, "OnboardingDefaultFragment notFirst: ${notFirst}")
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }

        binding.registerViewpager.adapter = RegisterPagerAdapter(requireActivity().supportFragmentManager, binding)
        binding.registerViewpager.offscreenPageLimit = 2
        binding.dotsIndicator.attachTo(binding.registerViewpager)
    }
}