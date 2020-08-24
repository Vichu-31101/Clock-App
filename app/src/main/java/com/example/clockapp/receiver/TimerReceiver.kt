package com.example.clockapp.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.clockapp.MainActivity
import com.example.clockapp.R


class TimerReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = "Timer"
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        val notifPlayer = MediaPlayer.create(context,
            R.raw.done
        )
        notifPlayer.isLooping = false

        try {
            createNotificationChannel(context)
            val notificationIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent, 0
            )
            builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Clock App")
                .setContentText("Timer done!")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.logo_foreground)
                .setContentIntent(pendingIntent)
            notificationManager.notify(100,builder.build())
            notifPlayer.start()

        } catch (e: Exception) {
            Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, "Timer Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }
}