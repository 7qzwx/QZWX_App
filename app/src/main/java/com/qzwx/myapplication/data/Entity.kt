package com.qzwx.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String,
    val iconResId: Int,
    val description: String
)