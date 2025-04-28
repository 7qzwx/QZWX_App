package com.qzwx.feature_wordsmemory.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// 4. 更新 WordRepository 类，添加按标签查询和更新标签的方法
class WordRepository(private val wordDao : WordDao) {
    val allWords : Flow<List<Word>> = wordDao.getAllWords()

    fun getWordsByTag(tag : String) : Flow<List<Word>> {
        return wordDao.getWordsByTag(tag)
    }

    suspend fun insert(word : Word) {
        wordDao.insert(word)
    }

    suspend fun getWordByName(word : String) : Word? {
        return wordDao.getWordByName(word)
    }

    suspend fun updateWordTag(id : Int, tag : String) {
        wordDao.updateWordTag(id, tag)
    }

    suspend fun deleteWord(id : Int) {
        wordDao.deleteWordById(id)
    }

    // 获取除当前单词外的 3 个随机单词
    suspend fun getRandomWordsExcluding(currentWordId : Int) : List<Word> {
        return wordDao.getRandomWordsExcluding(currentWordId)
    }

    fun getWordsByDate(startDate: String, endDate: String): Flow<List<Word>> {
        return wordDao.getWordsByDate(startDate, endDate)
    }
}
