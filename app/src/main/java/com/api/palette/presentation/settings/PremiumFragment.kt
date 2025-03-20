package com.api.palette.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentPremiumBinding
import com.api.palette.presentation.main.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PremiumFragment : Fragment() {
    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)

        (activity as? ServiceActivity)?.findViewById<View>(com.api.palette.R.id.bottomBar)?.visibility = View.GONE

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ServiceActivity)?.findViewById<View>(com.api.palette.R.id.bottomBar)?.visibility = View.VISIBLE
        _binding = null
    }
}
