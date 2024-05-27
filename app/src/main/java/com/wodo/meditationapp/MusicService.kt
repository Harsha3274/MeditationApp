package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.Image
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import java.lang.Exception

class MusicService : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int, playbackSpeed: Float) {
        val intent= Intent(baseContext,MusicList::class.java)
        val contentIntent=PendingIntent.getActivity(this,0,intent,0)

        val prevIntent=Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent=PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent=Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent=PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent=Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent=PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent=Intent(baseContext,NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent=PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImgArt(sleep.musicListPA[sleep.songPosition].path)
        val image=if (imgArt!=null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.play_icon)
        }


        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contentIntent)
            .setContentTitle("Song Title")
            .setContentText("Artist Name")
            .setSmallIcon(R.drawable.playlist_icon)
            .setLargeIcon(image)
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.arrow_left, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.arrow_right, "Next", nextPendingIntent)
            .addAction(R.drawable.arrow_back, "Exit", exitPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,mediaPlayer!!.duration.toLong())
                .build())
            mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(),playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build())
        }

        startForeground(13, notification)
    }
    fun createMediaPlayer(){
        try {
            if (sleep.musicService!!.mediaPlayer==null) sleep.musicService!!.mediaPlayer= MediaPlayer()
            sleep.musicService!!.mediaPlayer!!.reset()
            sleep.musicService!!.mediaPlayer!!.setDataSource(sleep.musicListPA[sleep.songPosition].path)
            sleep.musicService!!.mediaPlayer!!.prepare()
            sleep.binding.pauseCircle.setIconResource(R.drawable.pause_circle)
            sleep.musicService!!.showNotification(R.drawable.pause_circle,0F)
            sleep.binding.tvSeekBarStartNumber.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            sleep.binding.tvSeekBarendNumber.text= formatDuration(mediaPlayer!!.duration.toLong())
            sleep.binding.seekBarPA.progress=0
            sleep.binding.seekBarPA.max= mediaPlayer!!.duration
            sleep.nowPlayingId=sleep.musicListPA[sleep.songPosition].id

        }catch (e: Exception){
            return
        }
    }
    fun seekBarSetup(){
        runnable= Runnable {
            sleep.binding.tvSeekBarStartNumber.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            sleep.binding.seekBarPA.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
       if (focusChange<=0){
           //pause music
           sleep.binding.pauseCircle.setIconResource(R.drawable.play_icon)
           NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
           showNotification(R.drawable.play_icon,1F)
           sleep.isPlaying =false
           sleep.musicService!!.mediaPlayer!!.pause()
       }
        else
       {
           //play music
           sleep.binding.pauseCircle.setIconResource(R.drawable.pause_circle)
           NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_circle)
           showNotification(R.drawable.play_icon, 0F)
           sleep.isPlaying =true
           mediaPlayer!!.start()
       }
    }
}
