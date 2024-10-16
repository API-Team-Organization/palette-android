package com.api.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.rive.runtime.kotlin.RiveAnimationView
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {

    private lateinit var binding: FragmentOnboarding1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        val riveAnimationView: RiveAnimationView = binding.onBoarding1Animation
        riveAnimationView.setRiveResource(R.raw.message_icon_new)

        return binding.root
    }

}