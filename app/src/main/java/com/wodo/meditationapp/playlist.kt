package com.wodo.meditationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding

class playlist : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}