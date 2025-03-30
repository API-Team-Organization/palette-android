package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment() {
    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboarding1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.onBoarding1Animation.setRiveResource(R.raw.message_icon_new)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
