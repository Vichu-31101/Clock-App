package com.example.clockapp

import android.app.Application
import android.graphics.Path
import android.media.MediaPlayer

class GlobalAlarm: Application() {

    companion object{
        var mediaPlayer = MediaPlayer()
    }
}