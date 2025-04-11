package com.qzwx.feature_accountbook.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// 事务数据操作接口
@Dao
interface TransactionDao {
    // 插入一条交易记录，若存在冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction : TransactionEntity)

    // 查询指定日期范围内的交易记录
    @Query("SELECT * FROM transactionentity WHERE date BETWEEN :startDate AND :endDate") // 使用 "transactions" 解决 SQL 解析错误
    suspend fun getTransactionsBetweenDates(startDate : Long,
        endDate : Long) : List<TransactionEntity>
}

// 分类数据操作接口
@Dao
interface CategoryDao {
    // 根据父分类ID获取子分类
    @Query("SELECT * FROM category WHERE parentCategoryId = :parentId")
    suspend fun getSubCategoriesByParentId(parentId : Long) : List<Category>

    // 插入一条分类记录，若存在冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category : Category)

    // 根据类型查询分类
    @Query("SELECT * FROM Category WHERE type = :type")
    suspend fun getCategoriesByType(type : String) : List<Category>
}

// 钱包数据操作接口
@Dao
interface WalletDao {
    // 插入一条钱包记录，若存在冲突则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet : Wallet)

    // 查询所有钱包
    @Query("SELECT * FROM Wallet")
    suspend fun getAllWallets() : List<Wallet>
}
