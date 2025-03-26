package com.api.palette.presentation.main.settings.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.api.palette.databinding.FragmentNotificationBinding
import com.api.palette.presentation.main.ServiceActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.GONE
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        (activity as? ServiceActivity)?.findViewById<View>(R.id.bottomBar)?.visibility = View.VISIBLE
    }
}
