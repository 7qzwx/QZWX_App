package com.qzwx.diary.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

// DiaryViewModel.kt
class DiaryViewModel(private val diaryDao: DiaryDao) : ViewModel() {
    // 所有日记的 LiveData
    val allDiaries: LiveData<List<DiaryEntry>> = diaryDao.getAllDiaries()

    // 插入日记
    fun insertDiary(title: String, content: String) {
        viewModelScope.launch {
            val diaryEntry = DiaryEntry(
                title = title,
                content = content,
                timestamp = LocalDateTime.now().toString(),
                riqi = LocalDate.now().toString(),
                zishutongji = content.length
            )
            diaryDao.insertDiary(diaryEntry)
        }
    }

    // 更新日记
    fun updateDiary(diaryEntry: DiaryEntry) {
        viewModelScope.launch {
            diaryDao.updateDiary(diaryEntry)
        }
    }

    // 删除日记
    fun deleteDiary(diaryEntry: DiaryEntry) {
        viewModelScope.launch {
            diaryDao.deleteDiary(diaryEntry)
        }
    }

    // 根据 ID 获取日记
    fun getDiaryById(diaryId: Int): LiveData<DiaryEntry> {
        return diaryDao.getDiaryById(diaryId)
    }
}

// DiaryViewModelFactory.kt
class DiaryViewModelFactory(private val diaryDao: DiaryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DiaryViewModel(diaryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}