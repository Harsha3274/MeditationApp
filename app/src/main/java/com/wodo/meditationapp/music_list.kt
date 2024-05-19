package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.animation.keyframe.PathKeyframe
import com.wodo.meditationapp.databinding.ActivityMusicListBinding
import java.util.ArrayList

class MusicList : AppCompatActivity() {

    private lateinit var binding: ActivityMusicListBinding
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeLayout()

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
        if (requestCode==13){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){
        requestRuntimePermission()
        setTheme(R.style.Theme_MeditationApp)
        binding = ActivityMusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicList=ArrayList<String>()
        musicList.add("1 Song")
        musicList.add("2 Song")
        musicList.add("3 Song")
        musicList.add("4 Song")
        musicList.add("5 Song")
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager=LinearLayoutManager(this@MusicList)
        musicAdapter= MusicAdapter(this@MusicList, musicList)
        binding.musicRV.adapter= musicAdapter
        binding.totalSongs.text="Total Songs : "+musicAdapter.itemCount
    }
}
