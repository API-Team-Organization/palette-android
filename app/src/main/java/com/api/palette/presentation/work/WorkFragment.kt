package com.api.palette.presentation.work

import WorkPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.api.palette.databinding.FragmentWorkBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkFragment : Fragment() {
    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!
    private val tabTextList = listOf("포스터", "동영상")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)
        val viewPager: ViewPager2 = binding.viewPager
        val adapter = WorkPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(binding.workTabLayout, viewPager) { tab, pos ->
            tab.text = tabTextList[pos]
        }.attach()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
