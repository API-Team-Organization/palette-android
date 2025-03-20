package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.rive.runtime.kotlin.RiveAnimationView
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Onboarding2Fragment : Fragment() {
    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        val riveAnimationView: RiveAnimationView = binding.onBoarding2Animation
        riveAnimationView.setRiveResource(R.raw.document_icon_new)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
