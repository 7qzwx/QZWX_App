package com.qzwx.qzwxapp.data

import androidx.room.*

@Entity(tableName = "LinkEntity")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val url : String,
    val description : String
)