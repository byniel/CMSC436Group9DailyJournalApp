package com.example.dailyjournalgroup9

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.recording_dialog.view.*
import kotlinx.android.synthetic.main.recording_dialog.view.chronometer
import kotlinx.android.synthetic.main.recording_dialog.view.pause_button
import kotlinx.android.synthetic.main.recording_dialog.view.play_button
import kotlinx.android.synthetic.main.recording_player_dialog.view.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private lateinit var date: String
private lateinit var emotion: String
private lateinit var player : MediaPlayer
private lateinit var seekBar : SeekBar
private val mHandler = Handler()
private var lastProgress = 0

class LoggedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logged_layout)


        val intent = intent

        date = intent.getStringExtra("date").toString()
        Log.i(TAG, date)


        val imageView = findViewById<ImageView>(R.id.imageButton2)
        val logging = findViewById<TextView>(R.id.textLog)
        val path = File(applicationContext.getExternalFilesDir(null), "$date.jpg")
        val audiopath = File(applicationContext.getExternalFilesDir(null), "$date.mp3")
        Log.i(TAG, path.toString())



        if (path.exists()) {

        val bitMap = BitmapFactory.decodeFile(path.absolutePath)
            imageView.setImageBitmap(bitMap)

        }

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
            if (audiopath.exists()) {
                Toast.makeText(this, "SOUND SAVED", Toast.LENGTH_LONG).show()
                val mRecorderDialogView = LayoutInflater.from(this).inflate(R.layout.recording_player_dialog, null)
                val chronometer = mRecorderDialogView.chronometer
                val mRecorderDialogBuilder = AlertDialog.Builder(this)
                        .setView(mRecorderDialogView)
                        .setTitle("Recording")
                mRecorderDialogBuilder.show()
                seekBar = mRecorderDialogView.seekBar2
                seekBar.progress = 0

                mRecorderDialogView.play_button.setOnClickListener {
                    mRecorderDialogView.play_button.visibility = View.GONE
                    mRecorderDialogView.pause_button.visibility = View.VISIBLE
                    player = MediaPlayer()

                    try {
                        player.setDataSource(audiopath.toString())
                        player.prepare()
                        player.start()
                    } catch (e: IOException) {

                    }

                    seekBar.progress = lastProgress
                    player.seekTo(lastProgress)
                    seekBar.max = player.duration
                    seekBarUpdate()
                    chronometer.start()

                    player.setOnCompletionListener (MediaPlayer.OnCompletionListener {
                        chronometer.stop()
                        chronometer.base = SystemClock.elapsedRealtime()
                        player.seekTo(0)
                        mRecorderDialogView.play_button.visibility = View.VISIBLE
                        mRecorderDialogView.pause_button.visibility = View.GONE
                    })

                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            if (player != null && fromUser) {
                                player!!.seekTo(progress)
                                chronometer.base = SystemClock.elapsedRealtime() - player!!.currentPosition
                                lastProgress = progress
                            }
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                    })


                }

                mRecorderDialogView.pause_button.setOnClickListener {
                    try {
                        player.release()
                    } catch (e: Exception) {

                    }
                    chronometer.stop()
                }



            } else {
                Toast.makeText(this, "No Recording Saved", Toast.LENGTH_LONG).show()
            }
        }






//        val playButton = findViewById<Button>(R.id.submit)
//        playButton.setOnClickListener {
//            Toast.makeText(this, "PLAYING SOUND", Toast.LENGTH_LONG).show()
//        }
    }
    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (player != null) {
            val mCurrentPosition = player.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    companion object {

        private const val TAG = "Logged Activity"
    }
}