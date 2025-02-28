package com.qzwx.core.room.room_qiandaosystem

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Keep
@Entity(tableName = "check_in")
data class CheckIn(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val name : String,
    val experience : Int = 0,
    val days : Int = 0,
    val level : Int = 1,
    val lastCheckInDate : String="",
    var isLocked : Boolean = false,// 是否为上锁状态
    var consecutiveDays : Int = 0 // 连续签到天数
)

@Keep
@Entity(tableName = "check_in_history")
data class CheckInHistory(
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val checkInName : String,
    val date : String="",
    val experience : Int = 0,
    val checkInCount : Int = 0,
    val level : Int = 1
)
