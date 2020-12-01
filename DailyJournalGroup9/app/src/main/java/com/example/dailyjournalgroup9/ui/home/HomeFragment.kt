package com.example.dailyjournalgroup9.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.Entry
import com.example.dailyjournalgroup9.LoggedActivity
import com.example.dailyjournalgroup9.LoggingActivity
import com.example.dailyjournalgroup9.R
import com.example.dailyjournalgroup9.ui.calendar.CalendarFragment
import java.io.*
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
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })

        val log_button = root.findViewById<Button>(R.id.logging_button)
        val welcome_text = root.findViewById<TextView>(R.id.welcome_text)

        val dirNameDate: String =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val directory = File(requireContext().getExternalFilesDir(
            null), dirNameDate + "/")
        Log.i(null, directory.toString())
        lateinit var today : Entry
        val text = StringBuilder()
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

            today = Entry(emotion.toString(), text.toString(), Date(), null, null)
            log_button.text = getString(R.string.logged_button)
            welcome_text.text = getString(R.string.welcome_logged)
            log_button.setOnClickListener {
                val logged_activity_intent = Intent(context, LoggedActivity::class.java)
                if (today != null) {
                    logged_activity_intent.putExtra("text", today.text)
                    logged_activity_intent.putExtra("emotion", today.emotion)
                    logged_activity_intent.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                    )

                }
                startActivity(logged_activity_intent)
            }
        } else {
            log_button.text = getString(R.string.not_logged_button)
            welcome_text.text = getString(R.string.welcome_not_logged)
            log_button.setOnClickListener {
                val log_intent = Intent(context, LoggingActivity::class.java)
                startActivity(log_intent)
            }
        }

        //will need to check if there is a record for the user already...if there is, change text


        return root
    }
}