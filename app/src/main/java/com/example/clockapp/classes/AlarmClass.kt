package com.example.clockapp.classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AlarmClass {
    @PrimaryKey
    var id = 0
    var hours = 0
    var minutes = 0
    var repeatList = ""
}