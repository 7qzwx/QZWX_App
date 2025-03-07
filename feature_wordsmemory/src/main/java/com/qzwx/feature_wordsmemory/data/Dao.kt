package com.qzwx.feature_wordsmemory.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word : Word) // 插入单词

    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords() : Flow<List<Word>> // 获取所有单词，按字母排序

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByName(word : String) : Word? // 根据单词名称查询
}