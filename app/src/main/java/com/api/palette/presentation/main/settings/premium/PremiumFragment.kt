package com.api.palette.presentation.main.settings.premium

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentPremiumBinding
import com.api.palette.presentation.main.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PremiumFragment : Fragment() {
    private lateinit var binding: FragmentPremiumBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPremiumBinding.inflate(inflater, container, false)
        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.GONE
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.VISIBLE
    }
}
