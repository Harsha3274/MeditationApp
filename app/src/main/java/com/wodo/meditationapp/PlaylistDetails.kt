package com.wodo.meditationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding
import com.wodo.meditationapp.databinding.ActivityPlaylistDetailsBinding
import com.wodo.meditationapp.databinding.PlaylistViewBinding

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding

    companion object{
        var currentPlaylistPos: Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos=intent.extras?.get("index")as Int
    }
}