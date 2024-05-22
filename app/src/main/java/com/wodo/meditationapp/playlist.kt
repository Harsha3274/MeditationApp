package com.wodo.meditationapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding

class playlist : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backImagePlaylist.setOnClickListener{finish()}

    }
}