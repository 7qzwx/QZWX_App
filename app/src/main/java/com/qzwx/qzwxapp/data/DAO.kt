package com.qzwx.qzwxapp.data

import androidx.room.*
import kotlinx.coroutines.flow.*

@Dao
interface LinkDao {
    @Insert
    suspend fun insertLink(link : LinkEntity)

    @Insert
    suspend fun insertAll(links : List<LinkEntity>)

    @Update
    suspend fun updateLink(link : LinkEntity)

    @Query("DELETE FROM linkentity WHERE id = :id")
    suspend fun deleteLink(id : Int)

    @Query("DELETE FROM linkentity")
    suspend fun deleteAllLinks()

    @Query("SELECT * FROM linkentity")
    fun getAllLinks() : Flow<List<LinkEntity>>
}