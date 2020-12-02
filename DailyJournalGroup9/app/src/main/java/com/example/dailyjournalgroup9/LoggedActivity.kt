package com.example.dailyjournalgroup9

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LoggedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logged_layout)

        val intent = intent

        val imageView = findViewById<ImageView>(R.id.imageButton2)
        val logging = findViewById<TextView>(R.id.textLog)

        val emotion = intent.getStringExtra("emotion")
        val text = intent.getStringExtra("text")
        val date = intent.getStringExtra("date")

        val emotionView = findViewById<ImageView>(R.id.neutral)
        if (emotion == "sad") {
            emotionView.setImageResource(R.drawable.ic_sad)
        } else if (emotion == "notgreat") {
            emotionView.setImageResource(R.drawable.ic_notgreat)
        } else if (emotion == "neutral") {
            emotionView.setImageResource(R.drawable.ic_neutral)
        } else if (emotion == "content") {
            emotionView.setImageResource(R.drawable.ic_content)
        } else {
            emotionView.setImageResource(R.drawable.ic_veryhappy)
        }

        logging.text = text;

        val playButton = findViewById<Button>(R.id.recordingButton)
        playButton.setOnClickListener {
            Toast.makeText(this, "PLAYING SOUND", Toast.LENGTH_LONG).show()
        }
    }
}