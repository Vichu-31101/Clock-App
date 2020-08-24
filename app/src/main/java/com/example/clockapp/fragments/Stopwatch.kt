package com.example.clockapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clockapp.R
import com.example.clockapp.classes.StopwatchClass
import kotlinx.android.synthetic.main.fragment_stopwatch.*

class Stopwatch : Fragment() {
    lateinit var customHandler: Handler
    var startTime = 0L
    var flagTime = 0L
    var isRunning = false
    var firstTime = true
    var swAdapter = com.example.clockapp.adapter.swAdapter(mutableListOf())


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_stopwatch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        flagListView.apply {
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,true)
            // set the custom adapter to the RecyclerView
            adapter = swAdapter
        }


        customHandler = Handler()
        start.setOnClickListener {
            if(firstTime){
                startTime = SystemClock.uptimeMillis()
                flagTime = SystemClock.uptimeMillis()
                firstTime = false
            }
            isRunning = if(!isRunning){

                customHandler.postDelayed(clockTick,0)
                true
            } else{
                customHandler.removeCallbacks(clockTick)
                false
            }
        }
        reset.setOnClickListener {
            startTime = SystemClock.uptimeMillis()
            flagTime = SystemClock.uptimeMillis()
            swText.text = "0:0.00"
            swAdapter.deleteAll()
        }
        flag.setOnClickListener {
            if(isRunning){
                var flag = (SystemClock.uptimeMillis() - flagTime)/10
                var time = (SystemClock.uptimeMillis() - startTime)/10
                var flagText = calcTime(flag)
                var timeText = calcTime(time)
                var sw = StopwatchClass()
                sw.flag = flagText
                sw.time = timeText
                swAdapter.addData(sw)
                flagTime = SystemClock.uptimeMillis()
            }
        }
    }

    private var clockTick = object: Runnable {
        override fun run() {
            var timeElapsed = (SystemClock.uptimeMillis() - startTime)/10
            swText.text = calcTime(timeElapsed)
            customHandler.postDelayed(this, 0)
        }

    }

    fun calcTime(time: Long): String {
        val mins = ((time/100f)/60).toInt()
        val secs = ((time/100f)%60).toInt()
        val milli = ((time)%60)%100
        if(milli<10){
            return (""+mins+":"+secs+".0"+milli)
        }else{
            return (""+mins+":"+secs+"."+milli)
        }
    }


}