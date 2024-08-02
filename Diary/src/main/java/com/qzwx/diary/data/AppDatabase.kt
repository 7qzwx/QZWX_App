package com.qzwx.diary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 定义数据库，包括 DiaryDao
@Database(entities = [DiaryEntry::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    // 抽象方法，返回 DiaryDao 实例
    abstract fun diaryDao(): DiaryDao

    // 单例模式，确保只有一个 AppDatabase 实例
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 获取数据库实例的方法
        fun getDiaryDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // 上下文
                    AppDatabase::class.java, // 数据库类
                    "diary_database" // 数据库名称
                )
                    .addCallback(DatabaseCallback(context)) // 添加回调
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }}

