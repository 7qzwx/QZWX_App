package com.qzwx.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qzwx.myapplication.data.LinkDao
import com.qzwx.myapplication.data.LinkEntity
import com.qzwx.myapplication.data.WebDefaultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LinkViewModel(private val linkDao : LinkDao) : ViewModel() {

    init {
        // 插入默认数据
        viewModelScope.launch {
            // 检查数据库是否为空
            val links = linkDao.getAllLinks().firstOrNull() ?: emptyList()
            if (links.isEmpty()) {
                val defaultLinks = WebDefaultData.defaultLinks
                linkDao.insertAll(defaultLinks)
            }
        }
    }

    val allLinks : Flow<List<LinkEntity>> = linkDao.getAllLinks()

    fun insertLink(link : LinkEntity) {
        viewModelScope.launch {
            linkDao.insertLink(link)
        }
    }

    fun updateLink(link : LinkEntity) {
        viewModelScope.launch {
            linkDao.updateLink(link)
        }
    }

    fun deleteLink(id : Int) {
        viewModelScope.launch {
            linkDao.deleteLink(id)
        }
    }
}

class LinkViewModelFactory(private val linkDao : LinkDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(LinkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LinkViewModel(linkDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}