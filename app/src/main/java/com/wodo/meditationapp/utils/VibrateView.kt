package com.wodo.meditationapp.utils

import android.content.Context
import android.os.Build

import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.wodo.meditationapp.R

class VibrateView {
    companion object {
        fun vibrate(context: Context, view: View) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        350, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
            val animation=AnimationUtils.loadAnimation(context, R.anim.vibrate)
            view.startAnimation(animation)
        }
    }
}
