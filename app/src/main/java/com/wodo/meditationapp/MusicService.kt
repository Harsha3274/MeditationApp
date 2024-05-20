package com.wodo.meditationapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService:Service() {
    private var myBinder=MyBinder()
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }
    inner class MyBinder: Binder(){
        fun currentService():MusicService{
            return this@MusicService
        }
    }
}