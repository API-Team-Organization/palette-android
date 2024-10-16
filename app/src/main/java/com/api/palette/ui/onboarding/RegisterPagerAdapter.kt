package com.api.palette.ui.onboarding

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class RegisterPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = when (position) {
        0 -> Onboarding1Fragment()
        1 -> Onboarding2Fragment()
        else -> Onboarding3Fragment()
    }

    override fun getCount() = 3
}