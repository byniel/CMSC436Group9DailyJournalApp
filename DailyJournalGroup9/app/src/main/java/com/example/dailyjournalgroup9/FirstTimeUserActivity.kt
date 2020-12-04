package com.example.dailyjournalgroup9

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_first_time_user.*
import java.text.SimpleDateFormat
import java.util.*

class FirstTimeUserActivity : AppCompatActivity() {
    var formatDate = SimpleDateFormat("MMMM dd YYYY", Locale.US)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_user)


        btn_pick_birth_date.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener{ datePicker, i, i2, i3 ->

                val selectDate = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, i)
                selectDate.set(Calendar.MONTH, i2)
                selectDate.set(Calendar.DAY_OF_MONTH, i3)
                val date = formatDate.format(selectDate.time)
                textview_birth_date.text = date

            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        btn_getStarted.setOnClickListener {

            val userName = text_username.text.toString()
            val userBday = textview_birth_date.text.toString()

            if (userName.isEmpty() || userBday.isEmpty()) {

                Toast.makeText(this, "Fill in information", Toast.LENGTH_SHORT).show()


            } else {
                val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                editor.apply{
                    putString("USER_NAME", userName)
                    putString("USER_BDAY", userBday)
                    putBoolean("FirstTime", false)
                    putBoolean("DailyNotification", false)

                }
                editor.commit()
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish() // prevents from going back to splash screen again once app starts


            }


        }








    }
}