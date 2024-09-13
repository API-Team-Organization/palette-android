package com.example.palette.ui.main.work

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class WorkPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 2
    override fun createFragment(position: Int) = when (position) {
        0 -> WorkPosterFragment()
        else -> WorkVideoFragment()
    }
}

