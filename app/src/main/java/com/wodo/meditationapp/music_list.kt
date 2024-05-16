package com.wodo.meditationapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.animation.keyframe.PathKeyframe
import com.wodo.meditationapp.databinding.ActivityMusicListBinding

class MusicList : AppCompatActivity() {

    private lateinit var binding: ActivityMusicListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding = ActivityMusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shuffleBtnMusic.setOnClickListener {
            val intent=Intent(this@MusicList,sleep::class.java)
            startActivity(intent)
        }
        binding.favouriteBtnMusic.setOnClickListener{
            val intent=Intent(this@MusicList,favourite::class.java)
            startActivity(intent)
        }
        binding.playlistBtnMusic.setOnClickListener{
            val intent=Intent(this@MusicList,playlist::class.java)
            startActivity(intent)
        }
    }
    private fun requestRuntimePermission(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
