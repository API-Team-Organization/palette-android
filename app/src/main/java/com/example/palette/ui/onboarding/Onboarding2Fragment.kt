package com.example.palette.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.palette.R
import com.example.palette.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment : Fragment() {

    private lateinit var binding: FragmentOnboarding2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        val onboarding2: ImageView = binding.onBoarding2Animation
        val gifImage = DrawableImageViewTarget(onboarding2)
        Glide.with(this).load(R.drawable.onboarding2).into(gifImage)

        return inflater.inflate(R.layout.fragment_onboarding2, container, false)
    }

}