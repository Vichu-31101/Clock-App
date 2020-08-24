package com.example.clockapp

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.clockapp.fragments.Alarm
import com.example.clockapp.fragments.Stopwatch
import com.example.clockapp.fragments.Timer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var fragmentList : MutableList<Fragment> = ArrayList()
    var labels = listOf<String>("Alarm","Timer","Stop Watch")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ForegroundService.startService(this, "Clock is running...")
        //GlobalAlarm.mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
        GlobalAlarm.mediaPlayer.stop()
        var player = SoundPool(2, AudioManager.STREAM_ALARM,0)
        var soundId =player.load(this,R.raw.alarm,1)
        player.stop(soundId)
        //Main View
        val adapter = MyViewPagerAdapter(supportFragmentManager)
        fragmentList.add(Alarm())
        fragmentList.add(Timer())
        fragmentList.add(Stopwatch())
        adapter.addFragment(fragmentList,labels)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
        tabs.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {

            }

        })







}


class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

    private var fragmentList : MutableList<Fragment> = ArrayList()
    private var labelsList = listOf<String>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: MutableList<Fragment>,labels: List<String>){
        fragmentList = fragment
        labelsList = labels
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return labelsList[position]
    }

}
}