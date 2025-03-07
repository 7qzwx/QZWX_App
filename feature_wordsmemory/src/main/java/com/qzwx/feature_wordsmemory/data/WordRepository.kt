package com.qzwx.feature_wordsmemory.data

import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao : WordDao) {
    val allWords : Flow<List<Word>> = wordDao.getAllWords()

    suspend fun insert(word : Word) {
        wordDao.insert(word)
    }

    suspend fun getWordByName(word : String) : Word? {
        return wordDao.getWordByName(word)
    }
}