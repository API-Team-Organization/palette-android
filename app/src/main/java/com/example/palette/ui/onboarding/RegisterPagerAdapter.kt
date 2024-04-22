package com.example.palette.ui.onboarding

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.palette.databinding.FragmentOnboardingDefaultBinding

class RegisterPagerAdapter(fm: FragmentManager, binding: FragmentOnboardingDefaultBinding) : FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var position = 0
    private val binding = binding
    companion object {
        var buttonText = "다음"
    }
    override fun getItem(position: Int): Fragment {
        binding.onBoardingButton.text = buttonText

        this.position = position
        Log.d("RegisterPagerAdapter", "getItem position is ${position}")
        return when(position) {
            0 -> {
                Onboarding1Fragment()
            }
            1 -> {

                Onboarding2Fragment()
            }
            else -> {
                Onboarding3Fragment()
            }
        }
    }
    override fun getCount() =  3


}