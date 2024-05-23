package com.wodo.meditationapp

import android.media.MediaMetadataRetriever
import android.media.metrics.MediaMetricsManager
import android.os.Build
import android.provider.MediaStore.Audio.Artists
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import android.app.Service
import kotlin.system.exitProcess

data class Music(val id: String, val title: String, val album: String, val artist: String, val duration: Long=0,val path: String,
    val artUri: String)

fun formatDuration(duration: Long):String{
    val minutes=TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds=(TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-
            minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",minutes,seconds)
}
fun getImgArt(path: String):ByteArray?{
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}

 fun setSongPosition(increment: Boolean) {
     if (!sleep.repeat) {
         if (increment) {
             if (sleep.musicListPA.size - 1 == sleep.songPosition)
                 sleep.songPosition = 0
             else ++sleep.songPosition
         } else {
             if (0 == sleep.songPosition)
                 sleep.songPosition = sleep.musicListPA.size - 1
             else --sleep.songPosition
         }
     }
 }
     fun exitApplication() {
         if (!sleep.isPlaying && sleep.musicService != null) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                 sleep.musicService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
             } else {
                 sleep.musicService!!.stopForeground(true)
             }
             sleep.musicService!!.mediaPlayer!!.release()
             sleep.musicService = null
             exitProcess(1)
         }
     }

fun favouriteChecker(id: String):Int{
    sleep.isFavourite=false
    favourite.favouriteSongs.forEachIndexed { index, music ->
        if (id==music.id){
            sleep.isFavourite=true
            return index
        }
    }
    return -1
}