package com.api.palette.presentation.main.work.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.api.palette.presentation.main.work.WorkPosterFragment
import com.api.palette.presentation.main.work.WorkVideoFragment

class WorkPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int) = when (position) {
        0 -> WorkPosterFragment()
        else -> WorkVideoFragment()
    }
}
