package com.qzwx.diary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 定义 DiaryEntry 数据表的结构
@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // 自增长的主键
    val timestamp: String, // 保存点击保存时的时间
    val title: String, // 标题
    val content: String // 正文
)
