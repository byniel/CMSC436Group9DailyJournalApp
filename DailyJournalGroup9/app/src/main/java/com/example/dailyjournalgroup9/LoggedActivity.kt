package com.example.dailyjournalgroup9

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private lateinit var date: String
private lateinit var emotion: String

class LoggedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logged_layout)

        date = intent.getStringExtra("date").toString()
        Log.i(TAG, date)

        val emotionView = findViewById<ImageView>(R.id.neutral)
        val imageView = findViewById<ImageView>(R.id.imageButton2)
        val logging = findViewById<TextView>(R.id.textLog)
        val path = File(applicationContext.getExternalFilesDir(null), "$date.jpg")
        val audiopath = File(applicationContext.getExternalFilesDir(null), "$date.mp3")
        Log.i(TAG, path.toString())

        if (path.exists()) {

        val bitMap = BitmapFactory.decodeFile(path.absolutePath)
            imageView.setImageBitmap(bitMap)

        }

        if (audiopath.exists()) {
            Toast.makeText(this, "SOUND SAVED", Toast.LENGTH_LONG).show()
        }


//        val playButton = findViewById<Button>(R.id.submit)
//        playButton.setOnClickListener {
//            Toast.makeText(this, "PLAYING SOUND", Toast.LENGTH_LONG).show()
//        }
    }

    companion object {

        private const val TAG = "Logged Activity"
    }
}