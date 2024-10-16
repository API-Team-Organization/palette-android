package com.api.palette.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.api.palette.R

fun changeFragment(fragment: Fragment, supportFragmentManager: FragmentManager, addToBackStack: Boolean = false) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.mainContent, fragment)
    if (addToBackStack) {
        transaction.addToBackStack(null)
    }
    transaction.commit()
}

fun isRootFragment(supportFragmentManager: FragmentManager): Boolean {
    return supportFragmentManager.backStackEntryCount == 0
}