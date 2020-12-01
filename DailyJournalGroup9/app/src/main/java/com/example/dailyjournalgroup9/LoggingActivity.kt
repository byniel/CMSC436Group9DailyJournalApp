package com.example.dailyjournalgroup9

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import java.io.*
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
            //Toast.makeText(this, "Logged submitted", Toast.LENGTH_LONG).show()
            Toast.makeText(this, logging.text.toString(), Toast.LENGTH_LONG).show()

            //Need to set up a request permisson result situation
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PermissionInfo.PROTECTION_NORMAL);

            } else {
                Log.i(null, "permission already granted")
            }

            val dirNameDate: String =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            val directory = File(applicationContext.getExternalFilesDir(
               null), dirNameDate + "/")
            Log.e(null, directory.toString())
            val thepath = "/storage/emulated/0/Android/data/com.example.dailyjournalgroup9/files/"
            if (!directory?.mkdirs()!!) {
                Log.e(null, "Directory not created")
            }

            val logOutputStream: FileOutputStream
            var log = logging.text.toString()

            var outputStreamWriter = OutputStreamWriter(FileOutputStream(File(directory,getResources().getString(R.string.text_file))))
            outputStreamWriter.append(log)
            outputStreamWriter.close()

            outputStreamWriter = OutputStreamWriter(FileOutputStream(File(directory, getResources().getString(R.string.emotion_file))))
            outputStreamWriter.append(emotion)
            outputStreamWriter.close()

//            val logfile = File(directory, getResources().getString(R.string.text_file))
//
//
//            val lt = StringBuilder()
//            try {
//                val br = BufferedReader(FileReader(logfile))
//                var line = br.readLine()
//
//                while (line != null) {
//                    lt.append(line)
//                    lt.append('\n')
//                    line = br.readLine()
//                }
//                br.close()
//            } catch (e: IOException) {
//
//            }
//            //val inputStreamWriter = InputStreamReader(FileInputStream(File(directory, "log1.txt")))
//            //val readme = inputStreamWriter.read().toString()
//            Log.e(null, lt.toString())
            //inputStreamWriter.close()

//            val emotionOutputStream: FileOutputStream
//
//            emotionOutputStream = openFileOutput(dirNameDate + "/emotion.txt", Context.MODE_PRIVATE)
//            emotionOutputStream.write(log.toByteArray())
//            logOutputStream.close()

            val myIntent = Intent(this@LoggingActivity, MainActivity::class.java)
            this@LoggingActivity.startActivity(myIntent)
        }

    }
}
