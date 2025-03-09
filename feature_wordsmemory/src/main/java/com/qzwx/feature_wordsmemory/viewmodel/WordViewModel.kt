package com.qzwx.feature_wordsmemory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.data.WordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class WordViewModel(private val repository : WordRepository) : ViewModel() {
    val allWords : Flow<List<Word>> = repository.allWords

    // 添加一个可变的标签选择状态
    private val _selectedTag = MutableStateFlow("全部")
    val selectedTag = _selectedTag.asStateFlow()

    // 根据选择的标签获取单词列表
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredWords = selectedTag.flatMapLatest { tag ->
        when (tag) {
            "全部" -> repository.allWords
            else   -> repository.getWordsByTag(tag)
        }
    }

    // 设置选择的标签
    fun setSelectedTag(tag : String) {
        _selectedTag.value = tag
    }

    fun insert(word : Word) = viewModelScope.launch {
        // 将单词的首字母转为大写
        val capitalizedWord = word.copy(word = word.word.replaceFirstChar { it.uppercase() })
        repository.insert(capitalizedWord)
    }

    suspend fun getWordByName(word : String) : Word? {
        return repository.getWordByName(word)
    }

    // 添加检查单词是否存在的方法
    suspend fun wordExists(word : String) : Boolean {
        val existingWord = repository.getWordByName(word)
        return existingWord != null
    }

    fun updateWordTag(id : Int, tag : String) = viewModelScope.launch {
        repository.updateWordTag(id, tag)
    }

    // 添加删除单词的功能
    fun deleteWord(id : Int) = viewModelScope.launch {
        repository.deleteWord(id)
    }

    // 单词统计
    private val _allWordsCount = MutableStateFlow(0) // 全部单词数量
    val allWordsCount = _allWordsCount.asStateFlow()
    private val _newWordsCount = MutableStateFlow(0) // 生疏单词数量
    val newWordsCount = _newWordsCount.asStateFlow()
    private val _familiarWordsCount = MutableStateFlow(0) // 熟悉单词数量
    val familiarWordsCount = _familiarWordsCount.asStateFlow()
    private val _masteredWordsCount = MutableStateFlow(0) // 掌握单词数量
    val masteredWordsCount = _masteredWordsCount.asStateFlow()
    private val _toLearnWordsCount = MutableStateFlow(0) // 待学习单词数量
    val toLearnWordsCount = _toLearnWordsCount.asStateFlow()

    fun loadWordCounts() {
        viewModelScope.launch {
            // 获取“全部”单词数量
            repository.allWords.collect { _allWordsCount.value = it.size }
        }

        viewModelScope.launch {
            // 获取“生疏”单词数量
            repository.getWordsByTag("生疏").collect { _newWordsCount.value = it.size }
        }

        viewModelScope.launch {
            // 获取“熟悉”单词数量
            repository.getWordsByTag("熟悉").collect { _familiarWordsCount.value = it.size }
        }

        viewModelScope.launch {
            // 获取“掌握”单词数量
            repository.getWordsByTag("掌握").collect { _masteredWordsCount.value = it.size }
        }

        viewModelScope.launch {
            // 获取“待学习”单词数量
            repository.getWordsByTag("待学习").collect { _toLearnWordsCount.value = it.size }
        }
    }

    // 获取除当前单词外的 3 个随机单词
    suspend fun getRandomWordsExcluding(currentWordId : Int) : List<Word> {
        return repository.getRandomWordsExcluding(currentWordId)
    }

    // 获取指定日期范围内每个日期的单词数量
    suspend fun getWordsByDate(): Map<LocalDate, Int> {
        val startDate = LocalDate.now().minusMonths(12)
        val endDate = LocalDate.now()

        // 获取数据库中的所有单词
        val words = repository.getWordsByDate(startDate.toString(), endDate.toString()).first()

        // 按日期进行分组并计算每个日期的单词数量
        return words.groupBy { LocalDate.parse(it.insertDate) }
            .mapValues { it.value.size }
    }
}

class WordViewModelFactory(
    private val repository : WordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}