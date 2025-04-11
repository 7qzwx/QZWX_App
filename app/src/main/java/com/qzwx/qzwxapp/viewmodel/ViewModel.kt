package com.qzwx.qzwxapp.viewmodel

import androidx.lifecycle.*
import com.qzwx.qzwxapp.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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

    fun insertAll(links : List<LinkEntity>) {
        viewModelScope.launch {
            linkDao.insertAll(links)
        }
    }

    fun deleteAllLinks() {
        viewModelScope.launch {
            linkDao.deleteAllLinks()
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