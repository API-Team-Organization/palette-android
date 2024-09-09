package com.example.palette.ui.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.shortToast(message: String) {
    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}
