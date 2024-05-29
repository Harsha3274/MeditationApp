package com.wodo.meditationapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wodo.meditationapp.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

    companion object{

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding=FragmentNowPlayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener{
            if (sleep.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextBtnNP.setOnClickListener{
            setSongPosition(increment = true)
            sleep.musicService!!.createMediaPlayer()
            Glide.with(this)
                .load(sleep.musicListPA[sleep.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text=sleep.musicListPA[sleep.songPosition].title
            sleep.musicService!!.showNotification(R.drawable.pause_circle, 1F)
            playMusic()
        }
        binding.root.setOnClickListener{
            val intent= Intent(requireContext(),Playlist::class.java)
            intent.putExtra("index",sleep.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (sleep.musicService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songNameNP.isSelected=true
            Glide.with(this)
                .load(sleep.musicListPA[sleep.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text=sleep.musicListPA[sleep.songPosition].title
            if (sleep.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.pause_circle)
            else binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        }
    }
    private fun playMusic(){
        sleep.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_circle)
        sleep.musicService!!.showNotification(R.drawable.pause_circle, 1F)
        sleep.binding.arrowRightBtn.setIconResource(R.drawable.arrow_right)
        sleep.isPlaying=true
    }
    private fun pauseMusic(){
        sleep.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        sleep.musicService!!.showNotification(R.drawable.play_icon, 0F)
        sleep.binding.arrowRightBtn.setIconResource(R.drawable.play_icon)
        sleep.isPlaying=false
    }
}