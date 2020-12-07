package com.example.dailyjournalgroup9.ui.settings

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Debug
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailyjournalgroup9.R
private lateinit var notificationManager : NotificationManagerCompat
private lateinit var builder : NotificationCompat.Builder
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val intent = Intent(p0!!, RepeatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(p0, 200, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = NotificationCompat.Builder(p0, "all_notifications")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_import_contacts_24)
                .setContentTitle("JRNL Notification")
                .setContentText("Have you logged for today? :)")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager = NotificationManagerCompat.from(p0)

        if (p1!!.action.equals("MY_NOTIFICATION_MESSAGE")) {
            Log.i("ALARM", "HEY")
            notificationManager.notify(200, builder.build())
        }


    }

}
