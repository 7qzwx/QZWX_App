package com.qzwx.feature_wordsmemory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao() : WordDao // 提供 DAO 实例

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context : Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Feature_WordsMemory"
                )
                    .addCallback(WordDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // 数据库创建时候自动插入默认数据
    private class WordDatabaseCallback(private val context : Context) : RoomDatabase.Callback() {
        override fun onCreate(db : SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val wordDao = getDatabase(context).wordDao()
                // 插入默认数据
                populateDatabase(wordDao)
            }
        }

        private suspend fun populateDatabase(wordDao : WordDao) {
            defaultWords.forEach { word ->
                wordDao.insert(word)
            }
        }
    }

}

