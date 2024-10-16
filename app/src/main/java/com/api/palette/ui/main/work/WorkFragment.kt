package com.api.palette.ui.main.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.api.palette.databinding.FragmentWorkBinding
import com.google.android.material.tabs.TabLayoutMediator


class WorkFragment : Fragment() {
    private lateinit var binding: FragmentWorkBinding
    private val tabTextList = listOf("포스터", "동영상")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkBinding.inflate(inflater, container, false)


        // ViewPager2와 Adapter 설정
        val viewPager2: ViewPager2 = binding.viewPager
        val adapter = WorkPagerAdapter(requireActivity())
        viewPager2.adapter = adapter

        TabLayoutMediator(binding.workTabLayout, binding.viewPager) { tab, pos ->
            tab.text = tabTextList[pos]
        }.attach()


        return binding.root
    }
}
