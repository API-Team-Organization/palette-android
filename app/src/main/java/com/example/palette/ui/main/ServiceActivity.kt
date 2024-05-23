package com.example.palette.ui.main

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.controllers.RiveFileController
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import app.rive.runtime.kotlin.core.RiveEvent
import com.example.palette.R
import com.example.palette.common.Constant
import com.example.palette.databinding.ActivityServiceBinding
import com.example.palette.ui.main.create.CreateMediaFragment
import com.example.palette.ui.main.settings.SettingFragment
import com.example.palette.ui.main.work.WorkFragment

@OptIn(ExperimentalAssetLoader::class)
class ServiceActivity : AppCompatActivity() {
    private val binding by lazy { ActivityServiceBinding.inflate(layoutInflater) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeFragment(CreateMediaFragment())

        // RiveAnimationView에서 click_home 이벤트를 강제로 실행
        riveAnimationView.addEventListener(eventListener)

        setContentView(binding.root)
    }

    // bottomVisible 메서드 정의
    fun bottomVisible(isVisible: Boolean) {
        Log.d(Constant.TAG, "bottomVisible is ${isVisible}")
        if (isVisible) {
            // 하단 바를 보이게 하는 로직
            binding.bottomBar.visibility = View.VISIBLE
        } else {
            // 하단 바를 숨기는 로직
            binding.bottomBar.visibility = View.GONE
        }
    }




    private fun changeFragment(fragment: Fragment) {
        Log.d("MainFragment", "changeFragment is running")
        val fragmentManager = this.supportFragmentManager // 또는 requireActivity().supportFragmentManager (Fragment 내에서)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.mainContent, fragment)

        transaction.commit()
    }
}
