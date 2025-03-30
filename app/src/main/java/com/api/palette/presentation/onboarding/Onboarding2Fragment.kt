package com.api.palette.presentation.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment : Fragment() {
    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOnboarding2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.onBoarding2Animation.setRiveResource(R.raw.document_icon_new)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
