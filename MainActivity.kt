package com.example.myapplication

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import java.text.DateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var context: Context
    private var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        context = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mp = MediaPlayer.create(context, R.raw.sound1)
        val playButton = findViewById<ImageButton>(R.id.imageButton)
        val stopButton = findViewById<ImageButton>(R.id.imageButton2)

        playButton.setOnClickListener {
            try {
                if (mp?.isPlaying == true) {
                    mp?.stop()
                    mp?.release()
                    mp = MediaPlayer.create(context, R.raw.sound2) // Recreate MediaPlayer here
                }
                mp?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        stopButton.setOnClickListener {
            try {
                if (mp?.isPlaying == true) {
                    mp?.stop()
                    mp?.release()
                    mp = MediaPlayer.create(context, R.raw.sound2) // Recreate MediaPlayer here
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val timeDisplay = findViewById<TextView>(R.id.timeView)
        val timeThread = object : Thread() {
            override fun run() {
                try {
                    while (!isInterrupted) {
                        sleep(1000) // 1-second delay
                        runOnUiThread {
                            // Update the TextView with the current time
                            timeDisplay.text = DateFormat.getTimeInstance().format(Date())
                        }
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        timeThread.start() // Start the thread
    }
}
