package com.api.palette.presentation.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.api.palette.R

fun changeFragment(
    fragment: Fragment,
    manager: FragmentManager,
    addToBackStack: Boolean = false
) {
    manager.beginTransaction().apply {
        replace(R.id.mainContent, fragment)
        if (addToBackStack) addToBackStack(null)
        commit()
    }
}

fun isRootFragment(manager: FragmentManager): Boolean = manager.backStackEntryCount == 0
