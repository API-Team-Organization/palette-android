package com.api.palette.ui.util.progress

import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar

fun ProgressBar.setBigMax(max: Int) {
    this.max = max * 1000
}

fun ProgressBar.animateTo(progressTo: Int) {
    val animation = ObjectAnimator.ofInt(
        this,
        "progress",
        this.progress,
        progressTo * 1000
    )
    animation.duration = 1000
    animation.interpolator = DecelerateInterpolator()
    animation.start()
}