package com.qzwx.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.qzwx.feature_qiandaosystem.data.CheckIn
import com.qzwx.feature_qiandaosystem.data.CheckInHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {

    @Insert
    suspend fun insertLink(link: LinkEntity)

    @Insert
    suspend fun insertAll(links: List<LinkEntity>)

    @Update
    suspend fun updateLink(link: LinkEntity)

    @Query("DELETE FROM linkentity WHERE id = :id")
    suspend fun deleteLink(id: Int)

    @Query("DELETE FROM linkentity")
    suspend fun deleteAllLinks()

    @Query("SELECT * FROM linkentity")
    fun getAllLinks(): Flow<List<LinkEntity>>
}