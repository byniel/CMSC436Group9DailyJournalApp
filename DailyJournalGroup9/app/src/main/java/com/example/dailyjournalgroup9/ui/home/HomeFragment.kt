package com.example.dailyjournalgroup9.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.Entry
import com.example.dailyjournalgroup9.LoggedActivity
import com.example.dailyjournalgroup9.LoggingActivity
import com.example.dailyjournalgroup9.R
import com.example.dailyjournalgroup9.ui.calendar.CalendarFragment
import com.example.dailyjournalgroup9.ui.calendar.RobotoCalendarView
import com.example.dailyjournalgroup9.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.logentry_dialog.view.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)



        val textView = root.findViewById<TextView>(R.id.user_name)

        val sharedPreferences = activity?.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

        val name = sharedPreferences?.getString("USER_NAME", "No Name").toString()
        Log.i("TAG", name);
        textView.text = "Welcome " + name + "!"

        val log_button = root.findViewById<Button>(R.id.logging_button)
        val welcome_text = root.findViewById<TextView>(R.id.welcome_text)

        val dirNameDate: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val directory = File(
            requireContext().getExternalFilesDir(
                null
            ), dirNameDate + "/"
        )

        // Read in today's log to see if it exists
        lateinit var today : Entry
        val text = StringBuilder()

        // If entry exists for today, ask user if they want to check the log
        if (directory.exists() && directory.isDirectory) {
            val logfile = File(directory, getResources().getString(R.string.text_file))
            try {
                val br = BufferedReader(FileReader(logfile))
                var line = br.readLine()

                while (line != null) {
                    text.append(line)
                    text.append('\n')
                    line = br.readLine()
                }
                br.close()
            } catch (e: IOException) {

            }
            val emotion = StringBuilder()
            val emotionFile = File(directory, getResources().getString(R.string.emotion_file))
            try {
                val br = BufferedReader(FileReader(emotionFile))
                var line = br.readLine()

                while (line != null) {
                    emotion.append(line)
                    line = br.readLine()
                }
                br.close()
            } catch (e: IOException) {

            }

            today = Entry(emotion.toString(), text.toString(), Date(), false, false)
            log_button.text = getString(R.string.logged_button)
            welcome_text.text = getString(R.string.welcome_logged)
            log_button.setOnClickListener {
                val logged_activity_intent = Intent(context, LoggedActivity::class.java)
                if (today != null) {
                    logged_activity_intent.putExtra("text", today.text)
                    logged_activity_intent.putExtra("emotion", today.emotion)
                    logged_activity_intent.putExtra(
                        "date", SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.getDefault()
                        ).format(Date())
                    )

                }
                startActivity(logged_activity_intent)
            }

        // Otherwise, let the user add a log for today
        } else {
            log_button.text = getString(R.string.not_logged_button)
            welcome_text.text = getString(R.string.welcome_not_logged)
            log_button.setOnClickListener {
                val log_intent = Intent(context, LoggingActivity::class.java)
                log_intent.putExtra(
                    "date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(
                        Date()
                    )
                )
                startActivity(log_intent)
            }
        }

        return root
    }
}