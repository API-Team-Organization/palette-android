package com.example.palette.ui.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.palette.R
import com.example.palette.databinding.FragmentPremiumBinding
import com.example.palette.ui.main.ServiceActivity

class PremiumFragment : Fragment() {

    private lateinit var binding: FragmentPremiumBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPremiumBinding.inflate(inflater, container, false)
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.GONE

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as ServiceActivity).findViewById<View>(R.id.bottomBar).visibility = View.VISIBLE
    }
}