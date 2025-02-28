package com.qzwx.diary.room

import androidx.lifecycle.LiveData

/** Repository用来定义接口,充当"快递员",为了引入对appdatabase的依赖  */
interface DiaryRepository {
    suspend fun insertDiary(diaryEntry : DiaryEntry)
    suspend fun deleteDiary(diaryEntry : DiaryEntry)
    suspend fun updateDiary(diaryEntry : DiaryEntry)
    fun getAllDiaries() : LiveData<List<DiaryEntry>>
    suspend fun getDiaryById(diaryId : Int) : DiaryEntry?
}