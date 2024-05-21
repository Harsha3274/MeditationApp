package com.wodo.meditationapp

import android.media.MediaMetadataRetriever
import android.media.metrics.MediaMetricsManager
import android.provider.MediaStore.Audio.Artists
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

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

 fun setSongPosition(increment: Boolean){
    if (increment){
        if (sleep.musicListPA.size-1== sleep.songPosition)
            sleep.songPosition =0
        else ++sleep.songPosition
    }else{
        if (0== sleep.songPosition)
            sleep.songPosition = sleep.musicListPA.size-1
        else --sleep.songPosition
    }
}