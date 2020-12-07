package com.example.dailyjournalgroup9

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.logging_layout.*
import kotlinx.android.synthetic.main.picture_dialog.view.*
import kotlinx.android.synthetic.main.recording_dialog.*
import kotlinx.android.synthetic.main.recording_dialog.view.*
import kotlinx.android.synthetic.main.submit_dialog.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*


private lateinit var date: TextView
private lateinit var time: TextView
private var emotion = "neutral"
private lateinit var photoFile: File
private lateinit var image: Bitmap
private lateinit var recorder : MediaRecorder
private lateinit var player : MediaPlayer
private var lastProgress = 0
private var fileName = ""
private val mHandler = Handler()
private var pictureTaken = false
private var dateToEnter = ""
private lateinit var seek : SeekBar
private var recordedVoice = false
private var isPaused = false
private var havePlayed = false
private var saveRecording = false
class LoggingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logging_layout)

        // Implement functionality of the emoji buttons
        val sadButton = findViewById<ImageButton>(R.id.sad)
        sadButton.setOnClickListener {
            emotion = "sad"
            Toast.makeText(this, "Sad", Toast.LENGTH_SHORT).show()
        }
        val notgreatButton = findViewById<ImageButton>(R.id.notgreat)
        notgreatButton.setOnClickListener {
            emotion = "notgreat"
            Toast.makeText(this, "Not Great", Toast.LENGTH_SHORT).show()
        }
        val neutralButton = findViewById<ImageButton>(R.id.neutral)
        neutralButton.setOnClickListener {
            emotion = "neutral"
            Toast.makeText(this, "Neutral", Toast.LENGTH_SHORT).show()
        }
        val contentButton = findViewById<ImageButton>(R.id.content)
        contentButton.setOnClickListener {
            emotion = "content"
            Toast.makeText(this, "Content", Toast.LENGTH_SHORT).show()
        }
        val veryhappyButton = findViewById<ImageButton>(R.id.veryhappy)
        veryhappyButton.setOnClickListener {
            emotion = "veryhappy"
            Toast.makeText(this, "Very Happy", Toast.LENGTH_SHORT).show()
        }

        val intent = intent
        dateToEnter = intent.getStringExtra("date").toString()


        val cal = Calendar.getInstance()
        val dateInfo = dateToEnter.split("-")
        Log.i(null, dateInfo[0])
        Log.i(null, dateInfo[1])
        Log.i(null, dateInfo[2])
        cal.set(dateInfo[2].toInt(), dateInfo[1].toInt() - 1, dateInfo[0].toInt())
        val monthName = SimpleDateFormat("MMMM").format(cal.time) + " " + cal.get(Calendar.DAY_OF_MONTH)


        // Display views onto the application screen
        val dateView = findViewById<TextView>(R.id.displayDate)
        dateView.text = monthName

        val logging = findViewById<EditText>(R.id.textLog)
        val recordingButton = findViewById<Button>(R.id.recordingButton)
        val imageButton = findViewById<ImageButton>(R.id.imageButton2)

        if (intent.getStringExtra("text") != null) {
            val currText = intent.getStringExtra("text").toString()
            logging.setText(currText)
        }

        if (intent.getStringExtra("emotion") != null) {
            emotion = intent.getStringExtra("emotion").toString()
        }



        /***************************
         * AUDIO RECORDING LOGGING *
         ***************************/

        recordingButton.setOnClickListener {

            val mRecorderDialogView = LayoutInflater.from(this).inflate(
                R.layout.recording_dialog,
                null
            )
            val chronometer = mRecorderDialogView.chronometer
            val mRecorderDialogBuilder = AlertDialog.Builder(this)
                    .setView(mRecorderDialogView)
                    .setTitle("Recording")
            val mRecorderDialog = mRecorderDialogBuilder.show()
            // check permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 111
                )
            }

            mRecorderDialogView.start_button.setOnClickListener {

                seek = mRecorderDialogView.seekBar
                recorder = MediaRecorder()
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

                val path = applicationContext.getExternalFilesDir(null) as File
                fileName = path.absolutePath + "/temprecording.mp3"
                recordedVoice = true
//                Log.i(TAG, "file name: $fileName")

                recorder.setOutputFile(fileName)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                chronometer.base = SystemClock.elapsedRealtime()
                try {

                    recorder.prepare()
                    recorder.start()
                    chronometer.start()
                    seek.progress = 0
                    mRecorderDialogView.start_button.visibility = View.GONE
                    mRecorderDialogView.stop_button.visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "ERROR RECORDING", Toast.LENGTH_SHORT).show()
                }
            }

            mRecorderDialogView.stop_button.setOnClickListener {
                try {
                    recorder.stop()
                    chronometer.stop()
                    recorder.reset()
                    recorder.release()

                    chronometer.base = SystemClock.elapsedRealtime()
                    mRecorderDialogView.play_button.visibility = View.VISIBLE
                    mRecorderDialogView.stop_button.visibility = View.GONE
                    mRecorderDialogView.save_button.visibility = View.VISIBLE
                    mRecorderDialogView.cancel_button2.visibility = View.VISIBLE
                    seek.visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "ERROR STOPPING RECORDING", Toast.LENGTH_SHORT).show()
                }
            }

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
                        seek.progress = lastProgress
                        player.seekTo(lastProgress)
                        player.start()
                        chronometer.start()


                    } catch (e: IOException) {
                        Toast.makeText(this, "ERROR PLAYING RECORDING", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    player = MediaPlayer()
                    try {
                        havePlayed = true
                        seek.progress = 0
                        player.setDataSource(fileName)
                        player.prepare()
                        player.start()
                        player.seekTo(0)
                        lastProgress = 0
                        seek.max = player.duration
                        Log.i(TAG, "recording duration: " + player.duration.toString())
                        chronometer.start()
                        seekBarUpdate()


                    } catch (e: IOException) {
                        Toast.makeText(this, "ERROR PLAYING RECORDING", Toast.LENGTH_SHORT).show()
                    }

                }



                player.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                    chronometer.stop()
                    chronometer.base = SystemClock.elapsedRealtime()
                    player.seekTo(0)
                    lastProgress = 0
                    mRecorderDialogView.play_button.visibility = View.VISIBLE
                    mRecorderDialogView.pause_button.visibility = View.GONE
                })

                seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (player != null && fromUser) {
                            player.seekTo(progress)
                            chronometer.base =
                                SystemClock.elapsedRealtime() - player.currentPosition
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

            mRecorderDialog.cancel_button2.setOnClickListener {
                saveRecording = false
                mRecorderDialog.dismiss()
            }

            mRecorderDialog.save_button.setOnClickListener {
                saveRecording = true
                mRecorderDialog.dismiss()

            }

            mRecorderDialog.setOnDismissListener {
                if (recordedVoice && !saveRecording) {
                   recordedVoice = false
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

            }



        }

        /*******************
         * PICTURE LOGGING *
         *******************/

        imageButton.setOnClickListener {
            val mPictureDialogView = LayoutInflater.from(this).inflate(
                R.layout.picture_dialog,
                null
            )
            val mPictureDialogBuilder = AlertDialog.Builder(this)
                    .setView(mPictureDialogView)
                    .setTitle("Picture")
            val pictureDialog = mPictureDialogBuilder.show()

            // Take picture with camera
                mPictureDialogView.takePicture_button.setOnClickListener {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        requestPermissions(permissions, WRITE_PERMISSION_CODE)
                    } else {
                        takePicture()
                    }
                    pictureDialog.dismiss()
                }

            // Get picture from gallery
            mPictureDialogView.gallery_button.setOnClickListener {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    pickFromGallery()
                }
                pictureDialog.dismiss()
            }

            // Exit from photo dialog
            mPictureDialogView.cancel_button.setOnClickListener {   pictureDialog.dismiss() }

        }

        /**************************
         * SUBMIT BUTTON FUNCTION *
         **************************/

        val submitButton = findViewById<Button>(R.id.submit)
        submitButton.setOnClickListener {

            val mSubmitView = LayoutInflater.from(this).inflate(R.layout.submit_dialog, null)
            val mSubmitBuilder = AlertDialog.Builder(this)
                .setView(mSubmitView)
                .setTitle("Submit")
            val mSubmitDialog = mSubmitBuilder.show()

            mSubmitView.cancel_button1.setOnClickListener { mSubmitDialog.dismiss() }

            mSubmitView.submit_button1.setOnClickListener {

                //Need to set up a request permisson result situation
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PermissionInfo.PROTECTION_NORMAL
                    )

                } else {
                    Log.i(null, "permission already granted")
                }

                val dirNameDate: String = dateToEnter.toString()
                val directory = File(
                    applicationContext.getExternalFilesDir(
                        null
                    ), dirNameDate + "/"
                )
                Log.e(null, directory.toString())
                //val thepath = "/storage/emulated/0/Android/data/com.example.dailyjournalgroup9/files/"
                if (!directory.mkdirs()) {
                    Log.e(null, "Directory not created")
                }

                var log = logging.text.toString()

                var outputStreamWriter = OutputStreamWriter(
                    FileOutputStream(
                        File(
                            directory, resources.getString(
                                R.string.text_file
                            )
                        )
                    )
                )
                outputStreamWriter.append(log)
                outputStreamWriter.close()

                outputStreamWriter = OutputStreamWriter(
                    FileOutputStream(
                        File(
                            directory, resources.getString(
                                R.string.emotion_file
                            )
                        )
                    )
                )
                outputStreamWriter.append(emotion)
                outputStreamWriter.close()


                /********************
                 * RECORDING SAVING *
                 ********************/

                if (recordedVoice) {
                    val recordedAudioFile = File(fileName)
                    val path = applicationContext.getExternalFilesDir(null) as File
                    val recordFile = File(path.absolutePath + "/$dateToEnter.mp3")
                    Log.i(TAG, "file name: $fileName")
                    recordedAudioFile.renameTo(recordFile)

                }

                /******************
                 * PICTURE SAVING *
                 ******************/

                if(pictureTaken) {
                    var path = applicationContext.getExternalFilesDir(null).toString()
                    var file = File(path, "$dateToEnter.jpg")
                    try {
                        val stream = FileOutputStream(file)
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        stream.flush()
                        stream.close()

                    } catch (e: IOException) {
                        Toast.makeText(this, "EXCEPTION", Toast.LENGTH_LONG).show()
                    }

                }

                val myIntent = Intent(this@LoggingActivity, MainActivity::class.java)
                this@LoggingActivity.startActivity(myIntent)

            }


        }

    }

    private fun takePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_LONG).show()
        }

    }

    private fun getPhotoFile(fileName: String) : File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.i(TAG, "storage directory  $storageDirectory")
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            RECORD_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            WRITE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // retrieving photo file that was recently taken on camera
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
             image = data?.extras?.get("data")  as Bitmap
//             image = BitmapFactory.decodeFile(photoFile.absolutePath)

            val defaultImage = findViewById<ImageView>(R.id.defaultImage)
            defaultImage.setBackgroundResource(0)
            imageButton2.setImageBitmap(image)
            pictureTaken  = true

            // retrieving photo file from gallery
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            imageButton2.setImageURI(data?.data)
            val d = data?.data as Uri
            image = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, d)
            pictureTaken  = true
            defaultImage.setBackgroundResource(0)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (havePlayed && !isPaused) {
            val mCurrentPosition = player.currentPosition
            seek.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val RECORD_PERMISSION_CODE = 111
        private const val WRITE_PERMISSION_CODE = 69
        private const val REQUEST_CODE = 42
        private const val TAG = "Logging Activity"
    }
}
