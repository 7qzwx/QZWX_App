package com.qzwx.diary.data


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.qzwx.diary.data.AppDatabase
import com.qzwx.diary.data.DiaryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = AppDatabase.getDiaryDatabase(application).diaryDao()

    // 获取所有日记条目的 LiveData
    val diaryEntries: LiveData<List<DiaryEntry>> = diaryDao.getAllDiaries()

    // 插入日记的方法
    fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryDao.insertDiary(diaryEntry)
        }
    }
}
