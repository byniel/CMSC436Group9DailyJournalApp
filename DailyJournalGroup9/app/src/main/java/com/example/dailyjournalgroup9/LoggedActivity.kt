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
private var isPaused = false
private var havePlayed = false
class LoggedActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logged_layout)


        val intent = intent

        // Get date from intent
        date = intent.getStringExtra("date").toString()
        Log.i(TAG, date)

        // Put views on application screen and get paths for resources
        val imageView = findViewById<ImageView>(R.id.imageButton2)
        val logging = findViewById<TextView>(R.id.textLog)
        val path = File(applicationContext.getExternalFilesDir(null), "$date.jpg")
        val audiopath = File(applicationContext.getExternalFilesDir(null), "$date.mp3")
        Log.i(TAG, path.toString())



        if (path.exists()) {

        val bitMap = BitmapFactory.decodeFile(path.absolutePath)
            imageView.setImageBitmap(bitMap)

        }

        // Retrieve more data from the intent
        val emotion = intent.getStringExtra("emotion")
        val text = intent.getStringExtra("text")
        val date = intent.getStringExtra("date")

        // Dynamic functionality for display of emoji view in bottom left
        val emotionView = findViewById<ImageView>(R.id.neutral)
        if (emotion == "sad") {
            emotionView.setImageResource(R.drawable.ic_sad)
            emotionView.setBackgroundResource(R.drawable.emojibackgroundbuttonsad)
        } else if (emotion == "notgreat") {
            emotionView.setImageResource(R.drawable.ic_notgreat)
            emotionView.setBackgroundResource(R.drawable.emojibackgroundbuttonnotgreat)
        } else if (emotion == "neutral") {
            emotionView.setImageResource(R.drawable.ic_neutral)
            emotionView.setBackgroundResource(R.drawable.emojibackgroundbuttonneutral)
        } else if (emotion == "content") {
            emotionView.setImageResource(R.drawable.ic_content)
            emotionView.setBackgroundResource(R.drawable.emojibackgroundbuttoncontent)
        } else {
            emotionView.setImageResource(R.drawable.ic_veryhappy)
            emotionView.setBackgroundResource(R.drawable.emojibackgroundbuttonveryhappy)
        }

        val cal = Calendar.getInstance()
        val dateInfo = date!!.split("-");
        cal.set(dateInfo[2].toInt(), dateInfo[1].toInt() - 1, dateInfo[0].toInt())
        val monthName = SimpleDateFormat("MMMM").format(cal.time) + " " + cal.get(Calendar.DAY_OF_MONTH)
        val dateView = findViewById<TextView>(R.id.displayDate)
        dateView.text = monthName

        logging.text = text;

        val playButton = findViewById<Button>(R.id.recordingButton)

        playButton.setOnClickListener {
            if (audiopath.exists()) {
                val mRecorderDialogView = LayoutInflater.from(this).inflate(R.layout.recording_player_dialog, null)
                val chronometer = mRecorderDialogView.chronometer
                val mRecorderDialogBuilder = AlertDialog.Builder(this)
                        .setView(mRecorderDialogView)
                        .setTitle("Recording")
               val mRecorderDialog = mRecorderDialogBuilder.show()



                seekBar = mRecorderDialogView.seekBar2
                seekBar.progress = 0

                mRecorderDialogView.play_button.setOnClickListener {
                    mRecorderDialogView.play_button.visibility = View.GONE
                    mRecorderDialogView.pause_button.visibility = View.VISIBLE

                    if (isPaused) {
                        isPaused = false
                        player.start()
                        chronometer.base = SystemClock.elapsedRealtime() - player.currentPosition
                        chronometer.start()

                    } else if (havePlayed) {
                        try {
                            seekBar.progress = lastProgress
                            player.seekTo(lastProgress)
                            player.start()
                            chronometer.start()

                        } catch (e: IOException) {
                            Toast.makeText(this, "ERROR PLAYING RECORDING", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        player = MediaPlayer()
                        try {
                            seekBar.progress = 0
                            player.setDataSource(audiopath.toString())
                            player.prepare()
                            player.start()
                            player.seekTo(0)
                            lastProgress = 0
                            seekBar.max = player.duration
                            Log.i(TAG, "recording duration: " + player.duration.toString() )
                            chronometer.start()
                            seekBarUpdate()
                            havePlayed = true

                        } catch (e: IOException) {
                            Toast.makeText(this, "ERROR PLAYING RECORDING", Toast.LENGTH_SHORT).show()
                        }

                    }



                    player.setOnCompletionListener (MediaPlayer.OnCompletionListener {
                        chronometer.stop()
                        chronometer.base = SystemClock.elapsedRealtime()
                        player.seekTo(0)
                        lastProgress = 0
                        mRecorderDialogView.play_button.visibility = View.VISIBLE
                        mRecorderDialogView.pause_button.visibility = View.GONE
                    })

                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            if (player != null && fromUser) {
                                player.seekTo(progress)
                                chronometer.base = SystemClock.elapsedRealtime() - player.currentPosition
                                lastProgress = progress
                            }
                        }
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                        }
                    })


                }

                mRecorderDialogView.pause_button.setOnClickListener {
                    try {
                        isPaused = true
                        player.pause()
                        chronometer.stop()
                        mRecorderDialogView.play_button.visibility = View.VISIBLE
                        mRecorderDialogView.pause_button.visibility = View.GONE
                    } catch (e: Exception) {
                        Toast.makeText(this, "ERROR STOPPING RECORDING", Toast.LENGTH_LONG).show()
                    }
                }

                mRecorderDialog.setOnDismissListener {
                        if (havePlayed) {
                            havePlayed = false
                            player.stop()
                            player.release()
                            lastProgress = 0
                            if (isPaused) {
                                isPaused = false
                            }
                        }

                    }

            } else {
                Toast.makeText(this, "No Recording Saved", Toast.LENGTH_LONG).show()
            }


        }
    }
    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (havePlayed && !isPaused) {
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