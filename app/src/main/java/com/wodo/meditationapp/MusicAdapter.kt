package com.wodo.meditationapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wodo.meditationapp.databinding.MusicViewBinding

class MusicAdapter(private val context: Context,private var musicList: ArrayList<Music>, private val playlistDetails: Boolean=false,
    private val selectionActivity: Boolean=false): RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    class MyHolder(binding:MusicViewBinding):RecyclerView.ViewHolder(binding.root) {
        val title=binding.songNameMV
        val albums = binding.songAlbumMV
        val image=binding.imageMV
        val duration=binding.songDuration
        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text=musicList[position].title
        holder.albums.text=musicList[position].album
        holder.duration.text=formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
            .into(holder.image)
        when{
            playlistDetails->{
                holder.root.setOnClickListener{
                    sendIntent(ref="PlaylistDetailsAdapter", pos=position)
                }
            }
            selectionActivity->{
                holder.root.setOnClickListener{
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_500))
                }
            }
            else-> {
                holder.root.setOnClickListener{
                when{
                    MusicList.search->sendIntent(ref = "MusicAdapterSearch", pos = position)
                    musicList[position].id==sleep.nowPlayingId->
                        sendIntent(ref="NowPlaying",pos=sleep.songPosition)
                    else->sendIntent(ref="MusicAdapter",pos=position) }}
        }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun sendIntent(ref:String,pos: Int){
        val intent=Intent(context,Playlist::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
    private fun addSong(song: Music): Boolean{
        playlist.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed{ index, music ->
            if (song.id==music.id){
                playlist.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        playlist.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }
    fun refreshPlaylist(){
        musicList=ArrayList()
        musicList=playlist.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}

