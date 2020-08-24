package com.example.clockapp.fragments

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.clockapp.R
import com.example.clockapp.receiver.TimerReceiver
import kotlinx.android.synthetic.main.fragment_timer.*
import java.util.*


class Timer : Fragment() {

    var hours = 0
    var mins = 0
    var secs = 0
    var timeStop = false
    var isRunning = false
    var curTime = 0
    lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Timer Notification
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        start.setOnClickListener {
            val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            if(!hourInput.text.isNullOrEmpty()||!minInput.text.isNullOrEmpty()||!secInput.text.isNullOrEmpty()){
                hourInput.visibility = View.GONE
                minInput.visibility = View.GONE
                secInput.visibility = View.GONE
                hours = if(hourInput.text.isNullOrEmpty()){
                    0
                } else{
                    hourInput.text.toString().toInt()
                }
                mins = if(minInput.text.isNullOrEmpty()){
                    0
                } else{
                    minInput.text.toString().toInt()
                }
                secs = if(secInput.text.isNullOrEmpty()){
                    0
                } else{
                    secInput.text.toString().toInt()
                }
                var time = (hours*3600 + mins*60 + secs)*1000
                Log.d("test",time.toString())

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis+time-curTime, pendingIntent)


                isRunning = if(!isRunning){
                    timer = object: CountDownTimer((time-curTime).toLong(), 10L){
                        override fun onTick(millisUntilFinished: Long) {
                            if(!timeStop){
                                curTime = (time - millisUntilFinished).toInt()
                                var prog = (millisUntilFinished).toInt()

                                timerBar.progress = ((prog.toFloat()/time)*10000).toInt()
                                Log.d("time",timerBar.progress.toString())
                                var timerText = prog/1000
                                var h = (timerText/3600).toString()
                                var m = ((timerText%3600)/60).toString()
                                var s = ((timerText%3600)%60).toString()
                                if(h.length == 1){
                                    h = "0$h"
                                }
                                if(m.length == 1){
                                    m = "0$m"
                                }
                                if(s.length == 1){
                                    s = "0$s"
                                }
                                timeText.text = h+":"+m+":"+s
                            }
                        }
                        override fun onFinish() {
                            isRunning = false
                            timeText.text = ":    :"
                            timerBar.progress = 10000
                            curTime = 0
                            hourInput.visibility = View.VISIBLE
                            minInput.visibility = View.VISIBLE
                            secInput.visibility = View.VISIBLE
                        }
                    }.start()
                    true
                } else{
                    timer.cancel()
                    alarmManager.cancel(pendingIntent)
                    false
                }


                Log.d("test","$hours $mins $secs")

            }
            else{
                Toast.makeText(context,"Please enter a valid input",Toast.LENGTH_SHORT).show()
            }
        }

        reset.setOnClickListener {
            if(isRunning){
                timer.cancel()
                alarmManager.cancel(pendingIntent)
                isRunning = false
            }
            curTime = 0
            timeText.text = ":    :"
            timerBar.progress = 10000
            hourInput.visibility = View.VISIBLE
            minInput.visibility = View.VISIBLE
            secInput.visibility = View.VISIBLE
        }
    }

}