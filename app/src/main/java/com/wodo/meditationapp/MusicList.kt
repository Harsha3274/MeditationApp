// MusicList.kt
package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wodo.meditationapp.databinding.ActivityMusicListBinding
import java.io.File
import kotlin.system.exitProcess
import android.app.Service
import android.os.Build

class MusicList : AppCompatActivity() {

    private lateinit var binding: ActivityMusicListBinding
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        lateinit var MusicListMA: ArrayList<Music>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestRuntimePermission()
        setTheme(R.style.Theme_MeditationApp)
        binding = ActivityMusicListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (requestRuntimePermission())
            initializeLayout()
        initializeLayout()

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
            val intent = Intent(this@MusicList, playlist::class.java)
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

    override fun onDestroy() {
        super.onDestroy()
        if (!sleep.isPlaying && sleep.musicService != null) {
            exitApplication()
        }
    }
}
