package com.example.dailyjournalgroup9

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        iv_note.alpha = 0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction() {
            // action after splash screen ends


            val firstTime = checkFirstTime()
            Log.i("Check First Time", firstTime.toString())

            // if user's first time, go to FirstTimeUser screen
            if (firstTime) {
                val i = Intent(this, FirstTimeUserActivity::class.java)
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish() // prevents from going back to splash screen again once app starts

            } else {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish() // prevents from going back to splash screen again once app starts

            }

        }
    }


    // checks if user has used the app before
    private fun checkFirstTime(): Boolean {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("FirstTime", true)
    }
}