package com.example.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.rive.runtime.kotlin.RiveAnimationView
import com.example.palette.R
import com.example.palette.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment : Fragment() {

    private lateinit var binding: FragmentOnboarding2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        val riveAnimationView: RiveAnimationView = binding.onBoarding2Animation
        riveAnimationView.setRiveResource(R.raw.document_icon_new)

        return binding.root
    }

}