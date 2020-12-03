package com.example.dailyjournalgroup9.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dailyjournalgroup9.Entry
import com.example.dailyjournalgroup9.LoggedActivity
import com.example.dailyjournalgroup9.LoggingActivity
import com.example.dailyjournalgroup9.R
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
        moods_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                robotoCalendarView.clearMoods();
                if (position == 0) {
                    robotoCalendarView.currentMonth.setMoodFilter(null);
                } else if (position == 5) {
                    robotoCalendarView.currentMonth.setMoodFilter("sad");
                } else if (position == 4) {
                    robotoCalendarView.currentMonth.setMoodFilter("notgreat");
                } else if (position == 3) {
                    robotoCalendarView.currentMonth.setMoodFilter("neutral");
                } else if (position == 2) {
                    robotoCalendarView.currentMonth.setMoodFilter("content");
                } else if (position == 1) {
                    robotoCalendarView.currentMonth.setMoodFilter("veryhappy");
                }
                robotoCalendarView.drawMoodsFromFilter();
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })



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

        media_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                robotoCalendarView.clearMoods();
                if (position == 0) {
                    robotoCalendarView.currentMonth.setMediaFilter(null);
                } else if (position == 1) {
                    robotoCalendarView.currentMonth.setMediaFilter("text");
                } else if (position == 2) {
                    robotoCalendarView.currentMonth.setMediaFilter("audio");
                } else if (position == 3) {
                    robotoCalendarView.currentMonth.setMediaFilter("picture");
                }
                robotoCalendarView.drawMoodsFromFilter();
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        })

        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(this)

        robotoCalendarView.setShortWeekDays(false)

        robotoCalendarView.showDateTitle(true)

        robotoCalendarView.setDate(Date())

        return root
    }
//
//    protected fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
//    }

    // We'll need to fetch the data for that day and populate log with it
    override fun onDayClick(date: Date) {
        Toast.makeText(
            context,
            "onDayClick: $date",
            Toast.LENGTH_SHORT
        ).show()
        
        if (robotoCalendarView.currentMonth.getDay(date) != null) {
            val intentActivity = Intent(context, LoggedActivity::class.java)
            val dayEntry : Entry =  robotoCalendarView.currentMonth.getDay(date);
            intentActivity.putExtra("text", dayEntry.text);
            intentActivity.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dayEntry.date));
            intentActivity.putExtra("emotion", dayEntry.emotion);
            startActivity(intentActivity)
        } else {
            val logIntent = Intent (context, LoggingActivity::class.java)
            logIntent.putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date));
            startActivity(logIntent)
        }
        //if log doesn't exist, either do nothing or show toast
    }

    override fun onDayLongClick(date: Date) {
        Toast.makeText(
            context,
            "onDayLongClick: $date",
            Toast.LENGTH_SHORT
        ).show()
    }

    //remove moods from previous month
    override fun onRightButtonClick() {
        Toast.makeText(
            context,
            "onRightButtonClick!",
            Toast.LENGTH_SHORT
        ).show()

    }

    //remove moods from past month
    override fun onLeftButtonClick() {
        Toast.makeText(context, "onLeftButtonClick!", Toast.LENGTH_SHORT)
            .show()
    }

    companion object {

        private const val TAG = "Calendar Fragment"
    }
}