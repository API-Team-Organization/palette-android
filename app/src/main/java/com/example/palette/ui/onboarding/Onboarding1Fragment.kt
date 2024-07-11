package com.example.palette.ui.onboarding

import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.example.palette.R
import com.example.palette.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {

    private lateinit var binding: FragmentOnboarding1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        val onboarding1: ImageView = binding.onBoarding1Animation
        val gifImage = DrawableImageViewTarget(onboarding1)
        Glide.with(this).load(R.drawable.onboarding1).into(gifImage)

        return binding.root
    }

}