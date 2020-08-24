package com.example.clockapp.database

import androidx.room.*
import com.example.clockapp.classes.AlarmClass

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAlarm(alarm: AlarmClass)

    @Query("select * from AlarmClass")
    fun readAlarm() : List<AlarmClass>

    @Delete
    fun deleteAlarm(alarm: AlarmClass)

    @Query("Delete FROM AlarmClass WHERE id = :Id")
    fun deleteAlarmId(Id: Int)
}