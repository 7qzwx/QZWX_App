package com.qzwx.feature_wordsmemory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}