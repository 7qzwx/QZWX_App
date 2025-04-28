package com.qzwx.feature_accountbook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class, Category::class, Tag::class, LocationTag::class, Wallet::class, AccountBook::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao() : TransactionDao
    abstract fun categoryDao() : CategoryDao
    abstract fun walletDao() : WalletDao

    companion object {
        @Volatile
        private var instance : AppDatabase? = null
        fun getInstance(context : Context) : AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "Feature_AccountBook"
                ).build().also { instance = it }
            }
    }
}
