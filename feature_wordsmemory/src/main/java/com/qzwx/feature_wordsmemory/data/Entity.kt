package com.qzwx.feature_wordsmemory.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val id : Int = 0, // 主键，自增
    val word : String, // 单词名称
    val pos : String, // 词性
    val definition : String, // 释义
    val tag : String = "待学习", // 标签，默认为"待学习"
 val insertDate : String = LocalDate.now().toString() // 当前日期
)

