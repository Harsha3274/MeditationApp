package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.wodo.meditationapp.databinding.ActivityPlaylistBinding
import com.wodo.meditationapp.databinding.ActivityPlaylistDetailsBinding
import com.wodo.meditationapp.databinding.PlaylistViewBinding

@Suppress("DEPRECATION")
class PlaylistDetails : AppCompatActivity() {
    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPos: Int=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MeditationApp)
        binding=ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos=intent.extras?.get("index")as Int
        playlist.musicPlaylist.ref[currentPlaylistPos].playlist=
            checkPlaylist(playlist=playlist.musicPlaylist.ref[currentPlaylistPos].playlist)
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager=LinearLayoutManager(this)
        adapter= MusicAdapter(this,playlist.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter=adapter
        binding.backBtnPD.setOnClickListener{ finish() }
        binding.shuffleBtnPD.setOnClickListener{
            val intent = Intent(this, sleep::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "PlaylistDetailsShuffle")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener{
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removeAllPD.setOnClickListener{
            val builder=MaterialAlertDialogBuilder(this)
            builder.setTitle("Remove")
                .setMessage("Do you want to remove all songs from playlist?")
                .setPositiveButton("Yes"){dialog, _ ->
                    playlist.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog=builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text=playlist.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text="Total ${adapter.itemCount} Songs.\n\n"+
                "Created On:\n${playlist.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n"+
                " -- ${playlist.musicPlaylist.ref[currentPlaylistPos].createdOn}"
        if (adapter.itemCount>0)
        {
            Glide.with(this)
                .load(playlist.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility= View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //for storing favourites data using preferences
        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist=GsonBuilder().create().toJson(playlist.musicPlaylist)
        editor.putString("MusicPlaylist",jsonStringPlaylist)
        editor.apply()
    }
}