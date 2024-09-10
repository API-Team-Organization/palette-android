package com.example.palette.ui.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.palette.R

fun Fragment.shortToast(message: String) {
    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.changeFragment(fragment: Fragment) {
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.mainContent, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
}