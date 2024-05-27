package com.wodo.meditationapp

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> if (sleep.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)
            ApplicationClass.EXIT -> {
                exitApplication()
            }
        }
    }

    private fun playMusic() {
        sleep.isPlaying = true
        sleep.musicService!!.mediaPlayer!!.start()
        sleep.musicService!!.showNotification(R.drawable.pause_circle,1F)
        sleep.binding.pauseCircle.setIconResource(R.drawable.pause_circle)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_circle)
    }

    private fun pauseMusic() {
        sleep.isPlaying = false
        sleep.musicService!!.mediaPlayer!!.pause()
        sleep.musicService!!.showNotification(R.drawable.play_icon,0F)
        sleep.binding.pauseCircle.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment = increment)
        sleep.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(sleep.musicListPA[sleep.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
            .into(sleep.binding.backImageSleep)
        sleep.binding.songName.text = sleep.musicListPA[sleep.songPosition].title
        Glide.with(context)
            .load(sleep.musicListPA[sleep.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
            .into(NowPlaying.binding.songImgNP)
        NowPlaying.binding.songNameNP.text=sleep.musicListPA[sleep.songPosition].title
        playMusic()
        sleep.fIndex= favouriteChecker(sleep.musicListPA[sleep.songPosition].id)
        if (sleep.isFavourite)sleep.binding.favouriteBtnPA.setImageResource(R.drawable.favorite_icon)
        else sleep.binding.favouriteBtnPA.setImageResource(R.drawable.favorite_empty_icon)
    }
}
