package com.example.clockapp.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.clockapp.EnterActivity
import com.example.clockapp.GlobalAlarm
import com.example.clockapp.MainActivity
import com.example.clockapp.R
import java.util.*

class RepeatingAlarmReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "Alarm"
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : NotificationCompat.Builder
    var id = 0
    var mediaList = listOf<Int>(
        R.raw.alarm,
        R.raw.chimes,
        R.raw.gentle,
        R.raw.twin
    )

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        id = intent.getIntExtra("id",100)
        var option = intent.getIntExtra("option",0)
        GlobalAlarm.mediaPlayer.stop()
        GlobalAlarm.mediaPlayer.release()
        GlobalAlarm.mediaPlayer = MediaPlayer.create(context,mediaList[option])
        GlobalAlarm.mediaPlayer.isLooping = true
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean("alarmring",true).commit()
        try {
            createNotificationChannel(context)
            val notificationIntent = Intent(context, EnterActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, 0
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Clock App")
                .setContentText("Alarm!")
                .setSmallIcon(R.mipmap.logo_foreground)
                .setContentIntent(pendingIntent)
            notificationManager.notify(90,builder.build())
            GlobalAlarm.mediaPlayer.start()
            setNextAlarm(context,id,option)

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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setNextAlarm(context: Context, id: Int, option:Int){
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR,7)
        Log.d("test","Setting next alarm")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RepeatingAlarmReceiver::class.java)
        intent.putExtra("id",id)
        intent.putExtra("option",option)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}