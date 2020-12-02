package com.example.dailyjournalgroup9

import android.Manifest
import android.app.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.app.AlertDialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import kotlinx.android.synthetic.main.recording_dialog.view.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private lateinit var date: TextView
private lateinit var time: TextView
private var emotion = "neutral"
private lateinit var photoFile: File
private lateinit var image: Bitmap
//private var photoFileName =   SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()) + ".jpg"
private lateinit var recorder : MediaRecorder
private lateinit var player : MediaPlayer
private var lastProgress = 0
private var fileName = ""
private var currDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
private val mHandler = Handler()
private var pictureTaken = false
private lateinit var seekBar1 : SeekBar
class LoggingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.logging_layout)

        val sadButton = findViewById<ImageButton>(R.id.sad)
        sadButton.setOnClickListener {
            emotion = "sad"
            Toast.makeText(this, "Emotion for today: Sad", Toast.LENGTH_SHORT).show()
        }
        val notgreatButton = findViewById<ImageButton>(R.id.notgreat)
        notgreatButton.setOnClickListener {
            emotion = "notgreat"
            Toast.makeText(this, "Emotion for today: Not Great", Toast.LENGTH_SHORT).show()
        }
        val neutralButton = findViewById<ImageButton>(R.id.neutral)
        neutralButton.setOnClickListener {
            emotion = "neutral"
            Toast.makeText(this, "Emotion for today: Neutral", Toast.LENGTH_SHORT).show()
        }
        val contentButton = findViewById<ImageButton>(R.id.content)
        contentButton.setOnClickListener {
            emotion = "content"
            Toast.makeText(this, "Emotion for today: Content", Toast.LENGTH_SHORT).show()
        }
        val veryhappyButton = findViewById<ImageButton>(R.id.veryhappy)
        veryhappyButton.setOnClickListener {
            emotion = "veryhappy"
            Toast.makeText(this, "Emotion for today: Very Happy", Toast.LENGTH_SHORT).show()
        }

        val intent = intent
        val dateToEnter = intent.getStringExtra("date");

        val logging = findViewById<EditText>(R.id.textLog)
        val recordingButton = findViewById<Button>(R.id.recordingButton)
        val imageButton = findViewById<ImageButton>(R.id.imageButton2)


        // Audio Recording Logging

        recordingButton.setOnClickListener {

            val mRecorderDialogView = LayoutInflater.from(this).inflate(R.layout.recording_dialog, null)
            val chronometer = mRecorderDialogView.chronometer
            val mRecorderDialogBuilder = AlertDialog.Builder(this)
                    .setView(mRecorderDialogView)
                    .setTitle("Recording")
           mRecorderDialogBuilder.show()
            // check permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
            }
            seekBar1 = mRecorderDialogView.seekBar
            mRecorderDialogView.start_button.setOnClickListener {

                recorder = MediaRecorder()
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

                val path = applicationContext.getExternalFilesDir(null) as File

                fileName = path.absolutePath + "/$currDate.mp3"

                Log.i(TAG, "file name: $fileName")

                recorder.setOutputFile(fileName)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    recorder.prepare()
                    recorder.start()
                } catch (e: IOException) {

                }
                mRecorderDialogView.seekBar.progress = 0
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
                mRecorderDialogView.start_button.visibility = View.GONE
                mRecorderDialogView.stop_button.visibility = View.VISIBLE
            }

            mRecorderDialogView.stop_button.setOnClickListener {
                try {
                    recorder.stop()
                    recorder.release()
                } catch (e: IOException) {
                }
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
                mRecorderDialogView.play_button.visibility = View.VISIBLE
                mRecorderDialogView.stop_button.visibility = View.GONE
                mRecorderDialogView.seekBar.visibility = View.VISIBLE
            }

            mRecorderDialogView.play_button.setOnClickListener {
                mRecorderDialogView.play_button.visibility = View.GONE
                mRecorderDialogView.pause_button.visibility = View.VISIBLE
                player = MediaPlayer()

                try {
                    player.setDataSource(fileName)
                    player.prepare()
                    player.start()
                } catch (e: IOException) {

                }

                mRecorderDialogView.seekBar.progress = lastProgress
                player.seekTo(lastProgress)
                mRecorderDialogView.seekBar.max = player.duration
                seekBarUpdate()
                chronometer.start()

                player.setOnCompletionListener (MediaPlayer.OnCompletionListener {
                    chronometer.stop()
                    chronometer.base = SystemClock.elapsedRealtime()
                    player.seekTo(0)
                    mRecorderDialogView.play_button.visibility = View.VISIBLE
                    mRecorderDialogView.pause_button.visibility = View.GONE
                })

                seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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





        }


        // Picture Logging

        imageButton.setOnClickListener {
            val mPictureDialogView = LayoutInflater.from(this).inflate(R.layout.picture_dialog, null)
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
                        takePicture();
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

            val dirNameDate: String = dateToEnter.toString()
            val directory = File(applicationContext.getExternalFilesDir(
               null), dirNameDate + "/")
            Log.e(null, directory.toString())
            //val thepath = "/storage/emulated/0/Android/data/com.example.dailyjournalgroup9/files/"
            if (!directory?.mkdirs()!!) {
                Log.e(null, "Directory not created")
            }

            var log = logging.text.toString()

            var outputStreamWriter = OutputStreamWriter(FileOutputStream(File(directory,getResources().getString(R.string.text_file))))
            outputStreamWriter.append(log)
            outputStreamWriter.close()

            outputStreamWriter = OutputStreamWriter(FileOutputStream(File(directory, getResources().getString(R.string.emotion_file))))
            outputStreamWriter.append(emotion)
            outputStreamWriter.close()

//            val logfile = File(directory, getResources().getString(R.string.text_file))
//

            currDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

            var path = applicationContext.getExternalFilesDir(null).toString()
//            Log.i(TAG, "Path: $path")

            var file = File(path, "$currDate.jpg")
//
//            Log.i(TAG, "File: $file")

            try {
                val stream = FileOutputStream(file)
                if (pictureTaken) {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    stream.close()
                } else {
                    Toast.makeText(this, "bitmap is null", Toast.LENGTH_LONG).show()
                }

            } catch( e: IOException) {
                Toast.makeText(this, "image saved", Toast.LENGTH_LONG).show()
            }


            val myIntent = Intent(this@LoggingActivity, MainActivity::class.java)
            this@LoggingActivity.startActivity(myIntent)
        }

    }

    private fun takePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

//        Log.i(TAG, "photo file name: $photoFileName");
//        create a photo file using date
//        photoFile = getPhotoFile(photoFileName)

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
            imageButton2.setImageBitmap(image)
            pictureTaken  = true

            // retrieving photo file from gallery
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            imageButton2.setImageURI(data?.data)
            val d = data?.data as Uri
            image = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, d)
            pictureTaken  = true
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
        if (player != null) {
            val mCurrentPosition = player.currentPosition
            seekBar1.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val RECORD_PERMISSION_CODE = 111
        private const val WRITE_PERMISSION_CODE = 69
        private const val REQUEST_CODE = 42;
        private const val FILE_NAME = "photo1.jpg"
        private const val TAG = "Logging Activity"
    }
}
