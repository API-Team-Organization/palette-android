package com.api.palette.presentation.util

import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar

private const val PROGRESS_SCALE = 1000

fun ProgressBar.setBigMax(max: Int) {
    this.max = max * PROGRESS_SCALE
}

fun ProgressBar.animateTo(progressTo: Int, durationMillis: Long = 1000L) {
    val targetProgress = progressTo * PROGRESS_SCALE
    ObjectAnimator.ofInt(this, "progress", this.progress, targetProgress).apply {
        duration = durationMillis
        interpolator = DecelerateInterpolator()
        start()
    }
}
