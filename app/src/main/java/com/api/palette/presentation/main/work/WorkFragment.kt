package com.api.palette.presentation.main.work

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.api.palette.databinding.FragmentWorkBinding
import com.api.palette.presentation.main.work.adapter.WorkPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class WorkFragment : Fragment() {
    private lateinit var binding: FragmentWorkBinding
    private val tabTextList = listOf("포스터", "동영상")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentWorkBinding.inflate(inflater, container, false)
        setupViewPager()
        return binding.root
    }

    private fun setupViewPager() {
        val adapter = WorkPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.workTabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTextList[position]
        }.attach()
    }
}
