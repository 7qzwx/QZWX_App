package com.qzwx.feature_todoanddone.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val body: String
)