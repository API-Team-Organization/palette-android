package com.example.palette.ui.service

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import app.rive.runtime.kotlin.core.RiveEvent
import com.example.palette.R
import com.example.palette.databinding.FragmentMainBinding
import com.example.palette.ui.service.create.CreateMediaFragment
import com.example.palette.ui.service.settings.SettingFragment
import com.example.palette.ui.service.work.WorkFragment

@OptIn(ExperimentalAssetLoader::class)
class MainFragment : Fragment() {
    private val binding by lazy { FragmentMainBinding.inflate(layoutInflater) }
    private val riveAnimationView: RiveAnimationView by lazy(LazyThreadSafetyMode.NONE) {
        binding.bottomBar
    }

    val eventListener = object : RiveFileController.RiveEventListener {
        override fun notifyEvent(event: RiveEvent) {
            when (event.name) {
                "click_home" -> {
                    Log.d("MainFragment", "event home click")
                    changeFragment(CreateMediaFragment())
                }
                "click_search" -> {
                    Log.d("MainFragment", "event search click")
                    changeFragment(WorkFragment())
                }
                "click_setting" -> {
                    Log.d("MainFragment", "event setting click")
                    changeFragment(SettingFragment())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        changeFragment(CreateMediaFragment())
        riveAnimationView.addEventListener(eventListener)

        return binding.root
    }

    private fun changeFragment(fragment: Fragment) {
        Log.d("MainFragment", "changeFragment is running")
        val fragmentManager = requireActivity().supportFragmentManager // 또는 requireActivity().supportFragmentManager (Fragment 내에서)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainContent, fragment)

        transaction.commit()
    }
}
