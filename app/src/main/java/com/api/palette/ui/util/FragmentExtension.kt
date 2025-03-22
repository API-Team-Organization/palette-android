package com.api.palette.ui.util

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.api.palette.R

fun Fragment.shortToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun Fragment.changeFragment(fragment: Fragment) {
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.mainContent, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
}
