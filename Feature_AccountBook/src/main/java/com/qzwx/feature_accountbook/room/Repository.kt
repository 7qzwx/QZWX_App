package com.qzwx.feature_accountbook.room

class AccountRepository(
    private val transactionDao : TransactionDao,
    private val categoryDao : CategoryDao,
    private val walletDao : WalletDao
) {
    // 插入一条交易记录
    suspend fun insertTransaction(transaction : TransactionEntity) {
        transactionDao.insert(transaction)
    }

    // 查询指定日期范围内的交易记录
    suspend fun getTransactionsBetweenDates(startDate : Long,
        endDate : Long) : List<TransactionEntity> {
        return transactionDao.getTransactionsBetweenDates(startDate, endDate)
    }

    // 插入一条分类记录
    suspend fun insertCategory(category : Category) {
        categoryDao.insert(category)
    }

    // 插入一条钱包记录
    suspend fun insertWallet(wallet : Wallet) {
        walletDao.insert(wallet)
    }

    // 获取所有钱包
    suspend fun getAllWallets() : List<Wallet> {
        return walletDao.getAllWallets()
    }

    // 获取分类数据
    suspend fun getCategoriesByType(type : String) : List<String> {
        return categoryDao.getCategoriesByType(type).map { it.name } // 假设 name 是分类的名称字段
    }

    // 获取子分类数据
    suspend fun getSubCategoriesByCategory(category : String) : List<String> {
        // 通过 type 获取主分类
        val categories = categoryDao.getCategoriesByType(category)
        // 获取该主分类下的子分类
        val subCategories = categories.flatMap { categoryEntity ->
            categoryDao.getSubCategoriesByParentId(categoryEntity.id)
        }
        // 返回子分类名称列表
        return subCategories.map { it.name }
    }
}
