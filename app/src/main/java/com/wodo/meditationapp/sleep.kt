package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding
import com.wodo.meditationapp.databinding.ActivitySleepBinding
import kotlin.Exception
import kotlin.system.exitProcess

class sleep : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var musicListPA:ArrayList<Music>
        var songPosition: Int=0
        var isPlaying: Boolean=false
        var musicService: MusicService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivitySleepBinding
        var repeat: Boolean=false

        var min15: Boolean=false
        var min30: Boolean=false
        var min60: Boolean=false
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
        binding.backImageSleep.setOnClickListener{finish()}
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
        binding.seekBarPA.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        binding.repeatBtnPA.setOnClickListener{
            if (!repeat){
                repeat=true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
            }else{
                repeat=false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.red))
            }
        }
        binding.equalizerBtn.setOnClickListener{
            try {
                val eqIntent=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,13)
            }catch (e:Exception){
                Toast.makeText(this,"Equalizer feature not supported!!",Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtn.setOnClickListener{
            val timer= min15 || min30 || min60
            if (!timer) showBottomSheetDialog()
            else{
                val builder=MaterialAlertDialogBuilder(this)
            builder.setTitle("Stop Timer")
                .setMessage("Do you want to stop timer?")
                .setPositiveButton("Yes"){_, _ ->
                    min15=false
                    min30=false
                    min60=false
                    binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
                val customDialog=builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }
        binding.shareIcon.setOnClickListener{
            val shareIntent=Intent()
            shareIntent.action=Intent.ACTION_SEND
            shareIntent.type="audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing music file!!"))
        }
    }
    private fun setLayout(){
        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.play_icon).centerCrop())
            .into(binding.backImageSleep)
        binding.songName.text= musicListPA[songPosition].title
        if (repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
        if (min15 || min30 || min60)  binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
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
            musicService!!.showNotification(R.drawable.pause_circle)
            binding.tvSeekBarStartNumber.text= formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvSeekBarendNumber.text= formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seekBarPA.progress=0
            binding.seekBarPA.max= musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        }catch (e:Exception){
            return
        }
    }

    //Important function
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
        musicService!!.showNotification(R.drawable.pause_circle)
        isPlaying=true
        musicService!!.mediaPlayer!!.start()
    }
    private fun pauseMusic(){
        binding.pauseCircle.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder=service as MusicService.MyBinder
        musicService=binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService=null
    }

    override fun onCompletion(mp: MediaPlayer?) {
       setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        }catch (e:Exception){return}
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==13||resultCode== RESULT_OK)
            return
    }

    private fun showBottomSheetDialog(){
        val dialog=BottomSheetDialog(this@sleep)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music.kt will stop after 15 minutes",Toast.LENGTH_SHORT).show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
            min15=true
            Thread{Thread.sleep(15*60000) // 60000 ms=1 minute
            if (min15) exitApplication() }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music.kt will stop after 30 minutes",Toast.LENGTH_SHORT).show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
            min30=true
            Thread{Thread.sleep(30*60000)
                if (min30) exitApplication() }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music.kt will stop after 60 minutes",Toast.LENGTH_SHORT).show()
            binding.timerBtn.setColorFilter(ContextCompat.getColor(this,R.color.yellow))
            min60=true
            Thread{Thread.sleep(60*60000)
                if (min60) exitApplication() }.start()
            dialog.dismiss()
        }
    }
}