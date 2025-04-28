package com.qzwx.qzwxapp.data

import android.content.*
import androidx.room.*

@Database(
    entities = [LinkEntity::class],
    version = 1
)
abstract class WebAppDatabase : RoomDatabase() {
    abstract fun linkDao() : LinkDao

    companion object {
        @Volatile
        private var INSTANCE : WebAppDatabase? = null

        fun getDatabase(context : Context) : WebAppDatabase {
            return INSTANCE?:synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WebAppDatabase::class.java,
                    "web_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}