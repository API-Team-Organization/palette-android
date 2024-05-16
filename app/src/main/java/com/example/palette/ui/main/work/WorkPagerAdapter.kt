package com.example.palette.ui.main.work

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class WorkPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        // 페이지 수 반환
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        // 각 페이지에 해당하는 Fragment를 반환
        return when (position) {
            0 -> WorkPosterFragment()
            else -> WorkVideoFragment()
        }
    }
}

