package com.qzwx.feature_qiandaosystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Database是整个房子,里面存放所有Entity及其对应的Dao;
 *
 * 外面通过它来访问app的数据内容 */
@Database(entities = [CheckIn::class, CheckInHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Feature_QianDaoSystem"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


