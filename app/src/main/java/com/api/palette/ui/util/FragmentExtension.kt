package com.api.palette.ui.util

import androidx.fragment.app.Fragment

fun Fragment.shortToast(message: String) {
    requireActivity().shortToast(message)
}

fun Fragment.changeFragment(fragment: Fragment) {
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(com.api.palette.R.id.mainContent, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
}
