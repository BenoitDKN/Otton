package com.example.otton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val connect = findViewById<Button>(R.id.connect)
        connect.setOnClickListener {
            Toast.makeText(this,"aaaa", Toast.LENGTH_LONG).show()
        }
    }
}