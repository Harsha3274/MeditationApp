package com.wodo.meditationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodo.meditationapp.databinding.ActivityFavouriteBinding
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding

class favourite : AppCompatActivity() {
    private lateinit var binding:ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object{
        var favouriteSongs: ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favouriteSongs= checkPlaylist(favouriteSongs)
        binding.backImageFavourite.setOnClickListener{ finish() }
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this,4)
        adapter = FavouriteAdapter(this, favouriteSongs)
        binding.favouriteRV.adapter = adapter
        if (favouriteSongs.size<1) binding.shuffleBtnFA.visibility= View.INVISIBLE
        binding.shuffleBtnFA.setOnClickListener{
            val intent = Intent(this, sleep::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }

    }
}