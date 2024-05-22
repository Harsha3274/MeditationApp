package com.wodo.meditationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wodo.meditationapp.databinding.ActivityFavouriteBinding
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding

class favourite : AppCompatActivity() {
    private lateinit var binding:ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}