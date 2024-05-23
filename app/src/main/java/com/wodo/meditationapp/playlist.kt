package com.wodo.meditationapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding

class playlist : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter: PlaylistViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tempList=ArrayList<String>()
        tempList.add("Travel songs")
        tempList.add("Lets Enjoy the ")
        tempList.add("Travel songs")
        tempList.add("Travel songs")
        tempList.add("Travel songs")

        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this@playlist,2)
        adapter = PlaylistViewAdapter(this,)
        binding.playlistRV.adapter = adapter

        binding.backImagePlaylist.setOnClickListener{finish()}

    }
}