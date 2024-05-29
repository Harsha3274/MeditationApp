// MusicList.kt
package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodo.meditationapp.databinding.ActivityMusicListBinding
import java.io.File
import android.view.Menu
import android.widget.SearchView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class MusicList : AppCompatActivity() {

    private lateinit var binding: ActivityMusicListBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var search: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestRuntimePermission()
        setTheme(R.style.Theme_MeditationApp)
        binding = ActivityMusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (requestRuntimePermission()){
            initializeLayout()

            //for retrieving favourites data using preferences
            favourite.favouriteSongs= ArrayList()
            val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString=editor.getString("FavouriteSongs",null)
            val typeToken=object :TypeToken<ArrayList<Music>>(){}.type
            if (jsonString!=null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
                favourite.favouriteSongs.addAll(data)
            }
            playlist.musicPlaylist= MusicPlaylist()
            val jsonStringPlaylist=editor.getString("MusicPlaylist",null)
            if (jsonStringPlaylist!=null){
                val dataPlaylist: MusicPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
                playlist.musicPlaylist=dataPlaylist
            }
        }

        binding.shuffleBtnMusic.setOnClickListener {
            val intent = Intent(this@MusicList, sleep::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MusicList")
            startActivity(intent)
        }
        binding.favouriteBtnMusic.setOnClickListener {
            val intent = Intent(this@MusicList, favourite::class.java)
            startActivity(intent)
        }
        binding.playlistBtnMusic.setOnClickListener {
            val intent = Intent(this@MusicList, Playlist::class.java)
            startActivity(intent)
        }
    }

    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {
        search = false
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MusicList)
        musicAdapter = MusicAdapter(this@MusicList, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSongs.text = "Total Songs : ${musicAdapter.itemCount}"
    }

    @SuppressLint("Recycle")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val idC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val titleC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                    val albumC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    val pathC =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val albumIDC =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("Content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIDC).toString()

                    val music = Music(
                        id = idC,
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        duration = durationC,
                        path = pathC,
                        artUri = artUriC
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        if (!sleep.isPlaying && sleep.musicService != null) {
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data using preferences
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString=GsonBuilder().create().toJson(favourite.favouriteSongs)
        editor.putString("FavouriteSongs",jsonString)
        val jsonStringPlaylist=GsonBuilder().create().toJson(playlist.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in MusicListMA)
                        if (song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search = true
                    musicAdapter.updateMusicList(searchList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}




