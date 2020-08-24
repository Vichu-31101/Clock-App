package com.example.clockapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_enter.*
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlin.math.sqrt

class EnterActivity : AppCompatActivity() {private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(preferences.getBoolean("alarmring",false)){
            cancelAlarm()
            preferences.edit().putBoolean("alarmring",false).commit()
        }else{
            timer.start()
        }


    }
    fun cancelAlarm(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)

    }
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration += delta
            Log.d("test",acceleration.toString())
            if (acceleration > 50) {
                progressBar.progress += 1
            }
            if(progressBar.progress == 100){
                Toast.makeText(applicationContext, "Alarm cancelled", Toast.LENGTH_SHORT).show()

                startNextActivity()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    val timer = object: CountDownTimer(2000L, 10L){
        override fun onTick(millisUntilFinished: Long) {
            var prog = (millisUntilFinished).toInt()
            progressBar.progress = (((2000-prog.toFloat())/2000)*100).toInt()
        }
        override fun onFinish() {
            val intent = Intent(this@EnterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    fun startNextActivity(){
        sensorManager!!.unregisterListener(sensorListener)
        val intent = Intent(this@EnterActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}