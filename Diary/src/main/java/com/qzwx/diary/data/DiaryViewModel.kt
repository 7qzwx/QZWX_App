package com.qzwx.diary.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

//    删除日记的方法
fun deleteDiaryEntry(diaryEntry: DiaryEntry) {
    viewModelScope.launch {
        diaryDao.deleteDiary(diaryEntry) // 调用删除方法
    }
}

    // 更新日记的方法
    fun updateDiaryEntry(diaryId: Int, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diaryEntry = diaryDao.getDiaryByIdNonLive(diaryId)
            if (diaryEntry != null) {
                diaryEntry.title = title
                diaryEntry.content = content
                diaryDao.updateDiary(diaryEntry)
            }
        }
    }

    // 根据 ID 获取具体日记条目
    fun getDiaryEntryById(diaryId: Int): LiveData<DiaryEntry> {
        return diaryDao.getDiaryById(diaryId)
    }

    // ViewModelFactory(在MainActivity中使用，别删！)
    class DiaryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
                return DiaryViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
