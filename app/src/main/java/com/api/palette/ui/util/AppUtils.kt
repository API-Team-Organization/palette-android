package com.api.palette.ui.util

import androidx.fragment.app.FragmentManager

fun isRootFragment(supportFragmentManager: FragmentManager): Boolean {
    return supportFragmentManager.backStackEntryCount == 0
}
