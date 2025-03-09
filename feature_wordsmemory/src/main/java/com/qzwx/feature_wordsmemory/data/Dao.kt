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

    @Query("SELECT * FROM words WHERE tag = :tag ORDER BY word ASC")
    fun getWordsByTag(tag : String) : Flow<List<Word>> // 根据标签查询单词

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWordByName(word : String) : Word? // 根据单词名称查询

    @Query("UPDATE words SET tag = :tag WHERE id = :id")
    suspend fun updateWordTag(id : Int, tag : String) // 更新单词标签

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id : Int) // 根据ID删除单词

    @Query("SELECT COUNT(*) FROM words WHERE word = :word")
    suspend fun checkWordExists(word : String) : Int

    // 获取除当前单词外的 3 个随机单词
    @Query("SELECT * FROM words WHERE id != :currentWordId ORDER BY RANDOM() LIMIT 3")
    suspend fun getRandomWordsExcluding(currentWordId : Int) : List<Word>

    // 清空所有单词
    @Query("DELETE FROM words")
    suspend fun deleteAll()

    // 插入多个单词
    @Insert
    suspend fun insertAll(words : List<Word>)

    @Query("SELECT * FROM words WHERE insertDate BETWEEN :startDate AND :endDate ORDER BY insertDate ASC")
    fun getWordsByDate(startDate : String, endDate : String) : Flow<List<Word>>
}