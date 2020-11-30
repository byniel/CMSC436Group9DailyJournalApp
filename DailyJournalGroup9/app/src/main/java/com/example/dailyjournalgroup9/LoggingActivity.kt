package com.example.dailyjournalgroup9

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


private lateinit var date: TextView
private lateinit var time: TextView
private var emotion = "neutral"

class LoggingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logging_layout)

        val sadButton = findViewById<ImageButton>(R.id.sad)
        sadButton.setOnClickListener {
            emotion = "sad"
            Toast.makeText(this, "Emotion for today: sad", Toast.LENGTH_SHORT).show()
        }
        val notgreatButton = findViewById<ImageButton>(R.id.notgreat)
        notgreatButton.setOnClickListener {
            emotion = "notgreat"
            Toast.makeText(this, "Emotion for today: not great", Toast.LENGTH_SHORT).show()
        }
        val neutralButton = findViewById<ImageButton>(R.id.neutral)
        neutralButton.setOnClickListener {
            emotion = "neutral"
            Toast.makeText(this, "Emotion for today: neutral", Toast.LENGTH_SHORT).show()
        }
        val contentButton = findViewById<ImageButton>(R.id.content)
        contentButton.setOnClickListener {
            emotion = "content"
            Toast.makeText(this, "Emotion for today: content", Toast.LENGTH_SHORT).show()
        }
        val veryhappyButton = findViewById<ImageButton>(R.id.veryhappy)
        veryhappyButton.setOnClickListener {
            emotion = "veryhappy"
            Toast.makeText(this, "Emotion for today: very happy", Toast.LENGTH_SHORT).show()
        }

        val logging = findViewById<EditText>(R.id.textLog)

        val imageButton = findViewById<ImageButton>(R.id.imageButton2)

        val recordingButton = findViewById<Button>(R.id.recordingButton)

        val submitButton = findViewById<Button>(R.id.submit)
        submitButton.setOnClickListener {
            Toast.makeText(this, "Logged submitted", Toast.LENGTH_LONG).show()

            val dirNameDate: String =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

            val directory = File(applicationContext.getExternalFilesDir(null), dirNameDate)
            if (!directory?.mkdirs()) {
                Log.e(null, "Directory not created")
            }

            var log = logging.text.toString()

            val logOutputStream: FileOutputStream

            logOutputStream = openFileOutput(dirNameDate + "/log", Context.MODE_PRIVATE)
            logOutputStream.write(log.toByteArray())
            logOutputStream.close()

            val emotionOutputStream: FileOutputStream

            emotionOutputStream = openFileOutput(dirNameDate + "/emotion", Context.MODE_PRIVATE)
            emotionOutputStream.write(log.toByteArray())
            logOutputStream.close()

            val myIntent = Intent(this@LoggingActivity, MainActivity::class.java)
            this@LoggingActivity.startActivity(myIntent)
        }

    }
}