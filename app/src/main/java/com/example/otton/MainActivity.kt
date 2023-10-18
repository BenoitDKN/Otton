package com.example.otton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.media.AudioManager
import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private val AUDIO_SAMPLE_RATE = 44100 // Fréquence d'échantillonnage audio
    private val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC // Source audio (microphone)
    private val AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_FORMAT)
    private lateinit var audioRecord: AudioRecord
    private var isMeasuring = false
    private lateinit var capturedSoundLevel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
        audioRecord = AudioRecord(AUDIO_SOURCE, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_FORMAT, BUFFER_SIZE)

        //le code que j'ai ajouté :
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val buttonSetVolume = findViewById<Button>(R.id.connect)
        startMeasuring()
        buttonSetVolume.setOnClickListener {
            showCapturedSoundLevel()
            setNotificationVolumeToPercentage()
            //stopMeasuring()
        }



    }
    private fun setNotificationVolumeToPercentage() {
        if (::capturedSoundLevel.isInitialized) {
            try {
                val percentage = capturedSoundLevel.toInt()
                Toast.makeText(this, "percentage : $capturedSoundLevel", Toast.LENGTH_SHORT).show()
                val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
                val desiredVolume = (maxVolume * percentage) / 100

                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, desiredVolume, 0)
            } catch (e: NumberFormatException) {
                // La conversion de la chaîne en entier a échoué
                // Gérer l'erreur ici
                Toast.makeText(this, "conversion échouée", Toast.LENGTH_SHORT).show()
            }
        } else {
            // capturedSoundLevel n'est pas initialisé
            // Gérer l'erreur ici
            Toast.makeText(this, "initialization échouée", Toast.LENGTH_SHORT).show()
        }
    }

    // Méthode pour commencer la mesure
    private fun startMeasuring() {
        // Utilisez la variable soundLevelTextView pour mettre à jour le TextView
        isMeasuring = true
        audioRecord.startRecording()

        Thread {
            val buffer = ShortArray(BUFFER_SIZE)
            while (isMeasuring) {
                audioRecord.read(buffer, 0, BUFFER_SIZE)
                var sum = 0.0
                for (s in buffer) {
                    sum += s * s
                }
                val rms = Math.sqrt(sum / BUFFER_SIZE)
                val db = 20 * Math.log10(rms)
                runOnUiThread {
                    capturedSoundLevel = "Niveau sonore : %.2f dB".format(db)
                    println(capturedSoundLevel)
                }
            }
        }.start()
    }

    // Méthode pour arrêter la mesure
    private fun stopMeasuring() {
        isMeasuring = false
        audioRecord.stop()
    }
    private fun showCapturedSoundLevel() {
        if (::capturedSoundLevel.isInitialized) {
            Toast.makeText(this, "Niveau sonore capturé : $capturedSoundLevel", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Aucune valeur de niveau sonore à afficher.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (::audioRecord.isInitialized) {
            audioRecord.release()
        }
    }
}
