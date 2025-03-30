package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.application.PaletteApplication
import com.api.palette.databinding.FragmentOnboardingDefaultBinding
import com.api.palette.presentation.onboarding.adapter.OnboardingPagerAdapter

class OnboardingDefaultFragment : Fragment() {

    private var _binding: FragmentOnboardingDefaultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!PaletteApplication.prefs.isFirst) {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        } else {
            setupViewPager()
        }
    }

    private fun setupViewPager() {
        binding.registerViewpager.apply {
            adapter = OnboardingPagerAdapter(requireActivity().supportFragmentManager)
            offscreenPageLimit = 2
        }
        binding.dotsIndicator.attachTo(binding.registerViewpager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
