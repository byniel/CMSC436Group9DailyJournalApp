package com.example.dailyjournalgroup9

import android.app.Activity
import android.os.Bundle
import android.widget.*

private lateinit var date: TextView
private lateinit var time: TextView
private var emotion = "neutral"

class LoggingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logging_main)

        //date.text = intent.getStringExtra("date")
        //time.text = intent.getStringExtra("time")

        val sadButton = findViewById<ImageButton>(R.id.sad)
        sadButton.setOnClickListener {
            emotion = "sad"
        }
        val notgreatButton = findViewById<ImageButton>(R.id.notgreat)
        notgreatButton.setOnClickListener {
            emotion = "notgreat"
        }
        val neutralButton = findViewById<ImageButton>(R.id.neutral)
        neutralButton.setOnClickListener {
            emotion = "neutral"
        }
        val contentButton = findViewById<ImageButton>(R.id.content)
        contentButton.setOnClickListener {
            emotion = "content"
        }
        val veryhappyButton = findViewById<ImageButton>(R.id.veryhappy)
        veryhappyButton.setOnClickListener {
            emotion = "veryhappy"
        }

        val logging = findViewById<EditText>(R.id.textLog)

        val submitButton = findViewById<Button>(R.id.submit)
        submitButton.setOnClickListener {
            Toast.makeText(this, "Your log was: %s. Your emotion was %s".format(logging.getText().toString(), emotion), Toast.LENGTH_LONG).show()
        }

    }
}