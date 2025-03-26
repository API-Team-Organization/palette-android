package com.api.palette.presentation.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.api.palette.presentation.onboarding.Onboarding1Fragment
import com.api.palette.presentation.onboarding.Onboarding2Fragment
import com.api.palette.presentation.onboarding.Onboarding3Fragment

class OnboardingPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val onboardingFragments = listOf(
        Onboarding1Fragment(),
        Onboarding2Fragment(),
        Onboarding3Fragment()
    )

    override fun getItem(position: Int): Fragment = onboardingFragments[position]

    override fun getCount(): Int = onboardingFragments.size
}
