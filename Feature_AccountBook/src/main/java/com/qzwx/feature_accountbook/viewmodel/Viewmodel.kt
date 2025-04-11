package com.qzwx.feature_accountbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.qzwx.feature_accountbook.room.AccountRepository
import com.qzwx.feature_accountbook.room.Category
import com.qzwx.feature_accountbook.room.TransactionEntity
import com.qzwx.feature_accountbook.room.Wallet
import kotlinx.coroutines.Dispatchers

class AccountViewModel(private val repository : AccountRepository) : ViewModel() {
    // 插入交易记录
    fun insertTransaction(transaction : TransactionEntity) = liveData(Dispatchers.IO) {
        repository.insertTransaction(transaction)
        emit(Unit)
    }

    // 查询指定日期范围内的交易记录
    fun getTransactionsBetweenDates(startDate : Long, endDate : Long) = liveData(Dispatchers.IO) {
        val transactions = repository.getTransactionsBetweenDates(startDate, endDate)
        emit(transactions)
    }

    // 插入分类记录
    fun insertCategory(category : Category) = liveData(Dispatchers.IO) {
        repository.insertCategory(category)
        emit(Unit)
    }

    // 查询指定类型的分类（支出/收入/转账/债务）
    fun getCategoriesByType(type : String) = liveData(Dispatchers.IO) {
        val categories = repository.getCategoriesByType(type)
        emit(categories)
    }

    // 插入钱包记录
    fun insertWallet(wallet : Wallet) = liveData(Dispatchers.IO) {
        repository.insertWallet(wallet)
        emit(Unit)
    }

    // 获取所有钱包
    fun getAllWallets() = liveData(Dispatchers.IO) {
        val wallets = repository.getAllWallets()
        emit(wallets)
    }

    // 获取子分类
    fun getSubCategoriesByCategory(category : String?) = liveData(Dispatchers.IO) {
        val subCategories =
            category?.let { repository.getSubCategoriesByCategory(it) } ?: emptyList()
        emit(subCategories)
    }
}

class AccountViewModelFactory(private val repository : AccountRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}