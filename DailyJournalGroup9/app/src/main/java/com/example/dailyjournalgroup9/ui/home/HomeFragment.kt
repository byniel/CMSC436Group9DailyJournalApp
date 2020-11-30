package com.example.dailyjournalgroup9.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.LoggingActivity
import com.example.dailyjournalgroup9.R
import com.example.dailyjournalgroup9.ui.calendar.CalendarFragment

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
        var has_logged = false

        if (has_logged) {
            log_button.text = getString(R.string.logged_button)
            welcome_text.text = getString(R.string.welcome_logged)
            log_button.setOnClickListener {
                //change this to view the log when that class comes in
                val logged_activity_intent = Intent(context, LoggingActivity::class.java)
                startActivity(logged_activity_intent)
            }
        } else {
            log_button.text = getString(R.string.logged_button)
            welcome_text.text = getString(R.string.welcome_logged)
            log_button.setOnClickListener {
                val log_intent = Intent(context, LoggingActivity::class.java)
                startActivity(log_intent)
            }
        }

        //will need to check if there is a record for the user already...if there is, change text


        return root
    }
}