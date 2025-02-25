package com.qzwx.diary.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// 定义 DiaryEntry 数据表的结构
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自增长的主键
    val timestamp: String, // 保存点击保存时的时间
    val riqi:String, //日期
    var title: String, // 标题
    var zishutongji:Int,//字数统计
    var content: String // 正文
)
