package com.qzwx.diary.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// 定义操作 DiaryEntry 数据表的 DAO 接口
@Dao
interface DiaryDao {
    // 插入一条新的日记记录
    @Insert
    suspend fun insertDiary(diaryEntry: DiaryEntry)

    // 删除一条日记记录
    @Delete
    suspend fun deleteDiary(diaryEntry: DiaryEntry)

    // 更新一条日记记录
    @Update
    suspend fun updateDiary(diaryEntry: DiaryEntry)

    // 查询所有日记记录
    @Query("SELECT * FROM diary_entries")
    fun getAllDiaries(): LiveData<List<DiaryEntry>> // 返回 LiveData 类型

//    通过id查询找到数据
    @Query("SELECT * FROM diary_entries WHERE id = :diaryId")
    fun getDiaryById(diaryId: Int): LiveData<DiaryEntry>

    // 通过 ID 查询找到数据（非 LiveData 版本）
    @Query("SELECT * FROM diary_entries WHERE id = :diaryId")
    suspend fun getDiaryByIdNonLive(diaryId: Int): DiaryEntry?


    // 通过 ID 删除一条日记记录
    @Query("DELETE FROM diary_entries WHERE id = :diaryId")
    suspend fun deleteDiaryById(diaryId: Int)
}
