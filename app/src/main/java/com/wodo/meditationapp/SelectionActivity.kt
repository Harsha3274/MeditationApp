package com.wodo.meditationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding
import com.wodo.meditationapp.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionBinding
    private lateinit var adapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectionBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_MeditationApp)
        setContentView(binding.root)
        binding.selectionRV.setItemViewCacheSize(10)
        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.layoutManager= LinearLayoutManager(this)
        adapter= MusicAdapter(this,MusicList.MusicListMA, selectionActivity = true)
        binding.selectionRV.adapter=adapter
        binding.backImageSA.setOnClickListener{finish()}

        // for search view
        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MusicList.musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MusicList.MusicListMA)
                        if (song.title.lowercase().contains(userInput))
                            MusicList.musicListSearch.add(song)
                    MusicList.search = true
                    adapter.updateMusicList(searchList = MusicList.musicListSearch)
                }
                return true
            }
        })
    }
}