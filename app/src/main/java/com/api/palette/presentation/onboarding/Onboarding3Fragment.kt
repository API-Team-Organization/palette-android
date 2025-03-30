package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding3Binding

class Onboarding3Fragment : Fragment() {
    private var _binding: FragmentOnboarding3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.onBoarding3Animation.setRiveResource(R.raw.swipe)
        binding.onBoarding3Button.setOnClickListener {
            findNavController().navigate(R.id.action_onboardingDefaultFragment_to_startFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
