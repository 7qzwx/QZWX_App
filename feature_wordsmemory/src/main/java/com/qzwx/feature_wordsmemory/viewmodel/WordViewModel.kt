package com.qzwx.feature_wordsmemory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qzwx.feature_wordsmemory.data.Word
import com.qzwx.feature_wordsmemory.data.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WordViewModel(private val repository : WordRepository) : ViewModel() {
    val allWords : Flow<List<Word>> = repository.allWords

    fun insert(word : Word) = viewModelScope.launch {
        repository.insert(word)
    }

    suspend fun getWordByName(word : String) : Word? {
        return repository.getWordByName(word)
    }
}

class WordViewModelFactory(
    private val repository : WordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}