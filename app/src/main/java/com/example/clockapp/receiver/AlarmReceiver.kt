package com.example.clockapp.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.clockapp.EnterActivity
import com.example.clockapp.GlobalAlarm
import com.example.clockapp.MainActivity
import com.example.clockapp.R
import com.example.clockapp.database.AlarmDatabase

class AlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "Alarm"
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : NotificationCompat.Builder
    var mediaList = listOf<Int>(
        R.raw.alarm,
        R.raw.chimes,
        R.raw.gentle,
        R.raw.twin
    )

    override fun onReceive(context: Context, intent: Intent) {
        var option = intent.getIntExtra("option",0)
        var entityId = intent.getIntExtra("entity",1000)
        GlobalAlarm.mediaPlayer.stop()
        GlobalAlarm.mediaPlayer.release()
        GlobalAlarm.mediaPlayer = MediaPlayer.create(context,mediaList[option])
        GlobalAlarm.mediaPlayer.isLooping = true
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("alarmring",true).commit()

        try {
            createNotificationChannel(context)
            val notificationIntent = Intent(context, EnterActivity::class.java)
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, 0
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Clock App")
                .setContentText("Alarm!")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.logo_foreground)
                .setContentIntent(pendingIntent)
            notificationManager.notify(90,builder.build())
            GlobalAlarm.mediaPlayer.start()
            Thread{
                AlarmDatabase.getInstance(context).alarmDao().deleteAlarmId(entityId)
            }.start()

        } catch (e: Exception) {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, "Alarm Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }
}