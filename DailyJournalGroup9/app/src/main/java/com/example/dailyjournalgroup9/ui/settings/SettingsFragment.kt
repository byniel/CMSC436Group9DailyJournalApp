package com.example.dailyjournalgroup9.ui.settings

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.R
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var notificationOn = false
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)!!
        notificationOn = sharedPreferences.getBoolean("DailyNotification", false)
        Log.i("Settings", notificationOn.toString())

    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.i("create view", "creating")
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val reminderNotificationSwitch = root.findViewById<Switch>(R.id.switch1)


        reminderNotificationSwitch.isChecked = notificationOn

        reminderNotificationSwitch.setOnCheckedChangeListener { _, isChecked ->
//            val message = if (isChecked) "Switch on" else "Switch off"
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            val editor = sharedPreferences.edit()
            if (isChecked) {
                editor?.apply{
                    putBoolean("DailyNotification", true)
                }
                editor.commit()
                createNotificationChannel()
                // Daily Notification at 10:30pm
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 17)
                calendar.set(Calendar.SECOND, 30)

                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                alarmIntent.action = "MY_NOTIFICATION_MESSAGE"
                val pendingIntent = PendingIntent.getBroadcast(context, 200, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)


            } else {
                editor?.apply{
                    putBoolean("DailyNotification", false)
                }
                editor.commit()

                val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                alarmIntent.action = "MY_NOTIFICATION_MESSAGE"
                val pendingIntent = PendingIntent.getBroadcast(context, 200, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.cancel(pendingIntent)

            }

        }
        return root
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

    }
}