package com.example.clockapp.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clockapp.receiver.AlarmReceiver
import com.example.clockapp.R
import com.example.clockapp.receiver.RepeatingAlarmReceiver
import com.example.clockapp.classes.AlarmClass
import com.example.clockapp.database.AlarmDatabase

import kotlinx.android.synthetic.main.al_list_layout.view.*
import kotlinx.android.synthetic.main.sw_list_layout.view.time

class alAdapter(context: Context, private var list: MutableList<AlarmClass>)
    : RecyclerView.Adapter<AlViewHolder>() {
    val context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.al_list_layout, parent, false)
        return AlViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: AlViewHolder, position: Int) {
        val al = list[position]
        var h = al.hours
        var m = al.minutes.toString()
        var AMPM = "AM"
        if(h > 12){
            h -= 12
            AMPM = "PM"
        }else if(h == 0){
            h = 12
            AMPM = "PM"
        }
        if(m.length == 1){
            m = "0$m"
        }

        holder.view.time.text = h.toString()+":"+m
        holder.view.ampm.text = AMPM
        if(al.repeatList.isNotEmpty()){
            holder.view.repeat.visibility = View.VISIBLE
            var daysStr = ""
            val daysList = listOf<String>("S","M","T","W","T","F","S")
            for(i in al.repeatList)
            {
                daysStr += daysList[i.toString().toInt()-1]+" "
            }
            holder.view.repeat.text = daysStr
        }
        else{
            holder.view.repeat.visibility = View.GONE
        }
        holder.view.delete.setOnClickListener {
            //Delete alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, list[holder.adapterPosition].id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
            //Delete repeating
            var alRem = list[holder.adapterPosition]
            if(alRem.repeatList.isNotEmpty()){
                for(i in alRem.repeatList){
                    val intent = Intent(context, RepeatingAlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(context, alRem.id*10+i.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    alarmManager.cancel(pendingIntent)
                }
            }
            Thread{
                AlarmDatabase.getInstance(context).alarmDao().deleteAlarm(alRem)
            }.start()
            list.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = list.size

    fun addAlarm(al: AlarmClass){
        list.add(al)
        notifyItemInserted(list.size-1)
        Thread{
            AlarmDatabase.getInstance(context).alarmDao().saveAlarm(al)
        }.start()
    }

    fun addData(alList: List<AlarmClass>){
        list = alList as MutableList<AlarmClass>
        notifyDataSetChanged()
    }


}


class AlViewHolder(val view: View) :
    RecyclerView.ViewHolder(view)