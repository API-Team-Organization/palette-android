package com.example.palette.ui.util

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.palette.common.Constant

fun Fragment.shortToast(message: String) {
    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.log(message: String) {
    Log.d(Constant.TAG, message)
}