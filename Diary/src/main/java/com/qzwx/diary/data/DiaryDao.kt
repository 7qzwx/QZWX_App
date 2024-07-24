package com.qzwx.diary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// 定义操作 DiaryEntry 数据表的 DAO 接口
@Dao
interface DiaryDao {
    // 插入一条新的日记记录
    @Insert
    suspend fun insert(diaryEntry: DiaryEntry)

    // 查询所有日记记录
    @Query("SELECT * FROM diary_entries")
    suspend fun getAll(): List<DiaryEntry>
}
