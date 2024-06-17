package com.example.palette.ui.util

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.palette.common.Constant

fun Activity.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
fun Activity.log(message: String) {
    Log.d(Constant.TAG, message)
}