package com.example.dailyjournalgroup9

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.logging_main.*
import kotlinx.android.synthetic.main.picture_dialog.view.*
import java.io.File

private lateinit var date: TextView
private lateinit var time: TextView
private var emotion = "neutral"
private const val REQUEST_CODE = 42;
private lateinit var photoFile: File
private const val FILE_NAME = "photo.jpg"
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

        val recordButton = findViewById<Button>(R.id.recordingButton)

        recordButton.setOnClickListener {

        }

        val imageButton = findViewById<ImageButton>(R.id.imageButton2)
        imageButton.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.picture_dialog, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Picture")
            val alertDialog = mBuilder.show()
            mDialogView.takePicture_button.setOnClickListener {
                //            Toast.makeText(this, "Clicked image", Toast.LENGTH_SHORT).show()
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                photoFile = getPhotoFile(FILE_NAME)
//            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
                val fileProvider = FileProvider.getUriForFile(this, "com.example.dailyjournalgroup9.fileprovider", photoFile)
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
                if (takePicture.resolveActivity(this.packageManager) != null) {

                    startActivityForResult(takePicture, REQUEST_CODE)
                } else {
                    Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
                }

                alertDialog.dismiss()
            }

            mDialogView.gallery_button.setOnClickListener {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    pickFromGallery()
                }

                alertDialog.dismiss()
            }

            mDialogView.cancel_button.setOnClickListener {   alertDialog.dismiss() }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val image = data?.extras?.get("data") as Bitmap
            val image = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageButton2.setImageBitmap(image)
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageButton2.setImageURI(data?.data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getPhotoFile(fileName: String) : File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001

    }
}