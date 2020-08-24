package com.example.clockapp.fragments

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clockapp.receiver.AlarmReceiver
import com.example.clockapp.R
import com.example.clockapp.receiver.RepeatingAlarmReceiver
import com.example.clockapp.classes.AlarmClass
import com.example.clockapp.adapter.alAdapter
import com.example.clockapp.database.AlarmDatabase
import kotlinx.android.synthetic.main.clock_popup.*
import kotlinx.android.synthetic.main.fragment_alarm.*
import java.util.*

class Alarm : Fragment() {

    lateinit var clockPop: Dialog
    lateinit var alarmCalendar: Calendar
    lateinit var repeatingCalender : Calendar
    var alarmId = 1
    var repeatList = mutableListOf<Int>()
    lateinit var alAdapter: alAdapter
    lateinit var spinnerAdapter: ArrayAdapter<String>
    val alarmTypeList = listOf<String>("Alarm","Chimes","Gentle","Twin")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        spinnerAdapter = ArrayAdapter(requireContext(),R.layout.custom_spinner,resources.getStringArray(R.array.list))
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        clockPop = Dialog(requireContext())
        clockPop.setContentView(R.layout.clock_popup)
        alAdapter = alAdapter(requireContext(),mutableListOf())
        Thread{
            var savedList = AlarmDatabase.getInstance(requireContext()).alarmDao().readAlarm()
            if(!savedList.isNullOrEmpty()){
                alAdapter.addData(savedList)
                alarmId = savedList.last().id + 1
            }
        }.start()
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmListView.apply {
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
            // set the custom adapter to the RecyclerView
            adapter = alAdapter
        }


        addAlarm.setOnClickListener {
            clockPop.alarmType.adapter = spinnerAdapter
            val checkList = listOf<CheckBox>(clockPop._1,clockPop._2,clockPop._3,clockPop._4,clockPop._5,clockPop._6,clockPop._7)
            for(checkBox in checkList){
                checkBox.isChecked = false
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked){
                        if(!repeatList.contains(checkList.indexOf(checkBox)+1))
                        {
                            repeatList.add(checkList.indexOf(checkBox)+1)
                        }
                    }
                }
            }

            clockPop.save.setOnClickListener {
                var option = clockPop.alarmType.selectedItemPosition
                //Set normal alarm
                alarmCalendar = Calendar.getInstance()
                repeatingCalender = Calendar.getInstance()
                alarmCalendar.set(Calendar.SECOND,0)
                alarmCalendar.set(Calendar.MILLISECOND,0)
                repeatingCalender.set(Calendar.SECOND,0)
                repeatingCalender.set(Calendar.MILLISECOND,0)
                var alarm  = AlarmClass()
                alarm.hours = clockPop.timerPicker.hour
                alarm.minutes = clockPop.timerPicker.minute
                alarm.id = alarmId
                alarmCalendar.set(Calendar.HOUR_OF_DAY,alarm.hours)
                alarmCalendar.set(Calendar.MINUTE,alarm.minutes)
                repeatingCalender.set(Calendar.HOUR_OF_DAY,alarm.hours)
                repeatingCalender.set(Calendar.MINUTE,alarm.minutes)
                var timeDiff = alarmCalendar.timeInMillis - Calendar.getInstance().timeInMillis
                if(timeDiff <= 0){
                    alarmCalendar.add(Calendar.DAY_OF_YEAR,1)
                }
                setAlarm(option,alarmId)

                //Set Repeating Alarm
                var rL = ""
                if(repeatList.isNotEmpty()){
                    for(i in repeatList){
                        setRepeating(i, alarmId*10 + i,option)
                        rL += i.toString()
                    }
                }
                Log.d("test",clockPop.alarmType.selectedItemId.toString())

                //Update
                alarmId += 1
                alarm.repeatList = rL
                alAdapter.addAlarm(alarm)
                clockPop.cancel()
            }
            clockPop.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarm(option: Int,alarmEntityId: Int){
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("option",option)
        intent.putExtra("entity",alarmEntityId)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setRepeating(week: Int,id: Int,option: Int){
        repeatingCalender.set(Calendar.DAY_OF_YEAR,Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
        repeatingCalender.set(Calendar.DAY_OF_WEEK,week)
        val timeDiff = repeatingCalender.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if(timeDiff <= 0){
            repeatingCalender.add(Calendar.DAY_OF_YEAR,7)
        }
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RepeatingAlarmReceiver::class.java)
        intent.putExtra("id",id)
        intent.putExtra("option",option)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, repeatingCalender.timeInMillis, 7*24*60*60*1000, pendingIntent)
    }
}