package com.example.dailyjournalgroup9.ui.calendar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.Entry
import com.example.dailyjournalgroup9.LoggedActivity
import com.example.dailyjournalgroup9.LoggingActivity
import com.example.dailyjournalgroup9.R
import kotlinx.android.synthetic.main.logentry_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(), RobotoCalendarView.RobotoCalendarListener {

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var robotoCalendarView: RobotoCalendarView


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        calendarViewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Gets the calendar from the view
        robotoCalendarView = root.findViewById<RobotoCalendarView>(R.id.robotoCalendarPicker)

        val moods_spinner: Spinner = root.findViewById(R.id.moods_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            activity?.applicationContext!!,
            R.array.moods_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            moods_spinner.adapter = adapter
        }
        moods_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                robotoCalendarView.clearMoods()
                if (position == 0) {
                    robotoCalendarView.currentMonth.moodFilter = null
                } else if (position == 5) {
                    robotoCalendarView.currentMonth.moodFilter = "sad"
                } else if (position == 4) {
                    robotoCalendarView.currentMonth.moodFilter = "notgreat"
                } else if (position == 3) {
                    robotoCalendarView.currentMonth.moodFilter = "neutral"
                } else if (position == 2) {
                    robotoCalendarView.currentMonth.moodFilter = "content"
                } else if (position == 1) {
                    robotoCalendarView.currentMonth.moodFilter = "veryhappy"
                }
                robotoCalendarView.drawMoodsFromFilter()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }



        val media_spinner: Spinner = root.findViewById(R.id.media_spinner)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            activity?.applicationContext!!,
            R.array.media_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            media_spinner.adapter = adapter
        }

        media_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                robotoCalendarView.clearMoods()
                if (position == 0) {
                    robotoCalendarView.currentMonth.mediaFilter = null
                } else if (position == 1) {
                    robotoCalendarView.currentMonth.mediaFilter = "text"
                } else if (position == 2) {
                    robotoCalendarView.currentMonth.mediaFilter = "audio"
                } else if (position == 3) {
                    robotoCalendarView.currentMonth.mediaFilter = "picture"
                }
                robotoCalendarView.drawMoodsFromFilter()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(this)

        robotoCalendarView.setShortWeekDays(false)

        robotoCalendarView.showDateTitle(true)

        robotoCalendarView.setDate(Date())

        return root
    }

    // If an entry exists for the day, goes to the log activity displaying that info
    // Otherwise, asks if you want to add a log for the given day.
    override fun onDayClick(date: Date) {
        
        if (robotoCalendarView.currentMonth.getDay(date) != null) {
            val intentActivity = Intent(context, LoggedActivity::class.java)
            val dayEntry : Entry =  robotoCalendarView.currentMonth.getDay(date)
            intentActivity.putExtra("text", dayEntry.text)
            intentActivity.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dayEntry.date))
            intentActivity.putExtra("emotion", dayEntry.emotion)
            startActivity(intentActivity)
        } else {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.logentry_dialog, null)
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Alert")
            val mSubmitDialog = dialogBuilder.show()

                dialogView.no_button.setOnClickListener { mSubmitDialog.dismiss() }

                dialogView.yes_button.setOnClickListener {
                    val logIntent = Intent (context, LoggingActivity::class.java)
                    logIntent.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date))
                    startActivity(logIntent)
                    mSubmitDialog.dismiss()
                }
      }
    }

    // If an entry exists for the day, asks if you want to write over the entry
    override fun onDayLongClick(date: Date) {
        if (robotoCalendarView.currentMonth.getDay(date) != null) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.replaceentry_dialog, null)
            val dialogBuilder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Alert")
            val mSubmitDialog = dialogBuilder.show()

            dialogView.no_button.setOnClickListener { mSubmitDialog.dismiss() }

            dialogView.yes_button.setOnClickListener {
                val logIntent = Intent (context, LoggingActivity::class.java)
                logIntent.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date))
                val dayEntry : Entry =  robotoCalendarView.currentMonth.getDay(date)
                if (dayEntry?.text != null && dayEntry.text.trim().isNotEmpty()) {
                    logIntent.putExtra("text", dayEntry.text)
                }
                if (dayEntry?.emotion != null && dayEntry.emotion.trim().isNotEmpty()) {
                    logIntent.putExtra("emotion", dayEntry.emotion)
                }
                startActivity(logIntent)
                mSubmitDialog.dismiss()
            }
        }
    }

    override fun onRightButtonClick() {
    }

    override fun onLeftButtonClick() {
    }

    companion object {

        private const val TAG = "Calendar Fragment"
    }
}