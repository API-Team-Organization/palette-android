package com.api.palette.ui.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.api.palette.R
import com.google.android.material.snackbar.BaseTransientBottomBar

fun Fragment.shortToast(
    message: String,
    @BaseTransientBottomBar.Duration duration: Int = Toast.LENGTH_SHORT
) {
    requireActivity().shortToast(message, duration)
}

fun Fragment.changeFragment(fragment: Fragment) {
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.mainContent, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
}