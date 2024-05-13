package com.example.palette.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.palette.MainActivity
import com.example.palette.R
import com.example.palette.application.PaletteApplication
import com.example.palette.application.PreferenceManager
import com.example.palette.databinding.FragmentSettingBinding
import com.example.palette.ui.main.ServiceActivity


class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding
    private lateinit var anim: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        anim = AnimationUtils.loadAnimation(activity, R.anim.scale)
        with(binding) {
            profile.setOnClickListener {
                it.startAnimation(anim)
            }
            appInformation.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
//                        v.startAnimation(anim)
                        v.scaleX = 0.9f
                        v.scaleY = 0.9f
                    }
                    MotionEvent.ACTION_UP -> {
                        v.scaleX = 1f
                        v.scaleY = 1f
                    }
                }
                return@setOnTouchListener true
            }

            logout.setOnClickListener {
                PaletteApplication.prefs = PreferenceManager(requireContext().applicationContext)

                PaletteApplication.prefs.clearToken()
                val intent = Intent(activity, MainActivity::class.java)
                requireActivity().startActivity(intent)

                activity?.finish()
            }
        }
    }
}