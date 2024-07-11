package com.example.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.palette.R
import com.example.palette.databinding.FragmentOnboarding3Binding

class Onboarding3Fragment : Fragment() {
    private lateinit var binding: FragmentOnboarding3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding3Binding.inflate(inflater, container, false)

        val onboarding3: ImageView = binding.onBoarding3Animation
        val gifImage = DrawableImageViewTarget(onboarding3)
        Glide.with(this).load(R.drawable.onboarding3).into(gifImage)

        binding.onBoarding3Button.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }
        return binding.root
    }
}