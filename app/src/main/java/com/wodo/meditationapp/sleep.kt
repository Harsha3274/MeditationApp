package com.wodo.meditationapp

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding
import com.wodo.meditationapp.databinding.ActivitySleepBinding
import java.lang.Exception

class sleep : AppCompatActivity(), ServiceConnection {
    private lateinit var binding: ActivitySleepBinding

    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition: Int=0
        var isPlaying:Boolean=false
        var musicService: MusicService? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivitySleepBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //For starting service
        val intent= Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startActivity(intent)

        initializeLayout()
        binding.pauseCircle.setOnClickListener{
            if (isPlaying) pauseMusic()
            else playMusic()
        }
        binding.arrowLeftBtn.setOnClickListener{
            prevNextSong(increment = false)
        }
        binding.arrowRightBtn.setOnClickListener{
            prevNextSong(increment = true)
        }
    }
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
            .into(binding.backImageSleep)
        binding.songName.text= musicListPA[songPosition].title
    }
    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer==null) musicService!!.mediaPlayer= MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying=true
            binding.pauseCircle.setIconResource(R.drawable.pause_circle)
        }catch (e:Exception){
            return
        }
    }
    private fun initializeLayout(){
        songPosition=intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "MusicAdapter"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MusicList.MusicListMA)
                setLayout()
            }
            "MusicList"->{
                musicListPA= ArrayList()
                musicListPA.addAll(MusicList.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }
    private fun playMusic(){
        binding.pauseCircle.setIconResource(R.drawable.pause_circle)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.pauseCircle.setIconResource(R.drawable.music_player_icon)
        isPlaying=false
        musicService!!.mediaPlayer!!.pause()
    }
    private fun prevNextSong(increment:Boolean){
        if (increment){
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(increment=false)
            setLayout()
            createMediaPlayer()
        }
    }
    private fun setSongPosition(increment: Boolean){
        if (increment){
            if (musicListPA.size-1== songPosition)
                songPosition=0
            else ++songPosition
        }else{
            if (0== songPosition)
                songPosition= musicListPA.size-1
            else --songPosition
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        musicService=binder.currentService()
        createMediaPlayer()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }
}