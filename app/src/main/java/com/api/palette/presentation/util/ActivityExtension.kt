package com.api.palette.presentation.util

import android.app.Activity
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar

fun Activity.shortToast(message: String, @BaseTransientBottomBar.Duration duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
