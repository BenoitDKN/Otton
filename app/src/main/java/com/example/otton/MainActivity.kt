package com.example.otton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.media.AudioManager

class MainActivity : AppCompatActivity() {
    private lateinit var audioManager: AudioManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //le code que j'ai ajouté :
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val buttonSetVolume = findViewById<Button>(R.id.connect)
        buttonSetVolume.setOnClickListener {
            setNotificationVolumeToPercentage()
        }


    }
    //la fonction que j'utilise que j'ai ajouté :
    private fun setNotificationVolumeToPercentage() {
        val percentage = 50
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
        val desiredVolume = (maxVolume*percentage) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, desiredVolume, 0)
    }
}

