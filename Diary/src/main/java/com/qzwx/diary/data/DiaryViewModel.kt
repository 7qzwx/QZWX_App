package com.qzwx.diary.data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = AppDatabase.getDiaryDatabase(application).diaryDao()

    // 创建 MutableLiveData 以存储日记条目
    private val _diaryEntries = MutableLiveData<List<DiaryEntry>>()
    val diaryEntries: LiveData<List<DiaryEntry>> get() = _diaryEntries // 提供只读的 LiveData

    init {
        // 初始化时加载日记条目
        viewModelScope.launch {
            _diaryEntries.value = diaryDao.getAllDiariesNonLive() // 获取初始数据
        }
    }

    // 插入日记的方法
    fun insertDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryDao.insertDiary(diaryEntry)
            fetchDiaryEntries() // 插入后重新加载数据
        }
    }

    // 删除日记的方法
    fun deleteDiaryEntry(diaryEntry: DiaryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            diaryDao.deleteDiary(diaryEntry)
            fetchDiaryEntries() // 删除后重新加载数据
        }
    }

    // 根据 ID 获取具体日记条目
    fun getDiaryEntryById(diaryId: Int): LiveData<DiaryEntry> {
        return diaryDao.getDiaryById(diaryId)
    }

    // 更新日记的方法
    fun updateDiaryEntry(diaryId: Int, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val diaryEntry = diaryDao.getDiaryByIdNonLive(diaryId)
            if (diaryEntry != null) {
                diaryEntry.title = title
                diaryEntry.content = content
                diaryDao.updateDiary(diaryEntry)
                fetchDiaryEntries() // 更新后重新加载数据
            }
        }
    }

    // 重新加载数据的方法
    fun fetchDiaryEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            val newEntries = diaryDao.getAllDiariesNonLive()
            _diaryEntries.postValue(newEntries)
        }
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
