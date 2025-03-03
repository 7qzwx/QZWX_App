package com.qzwx.feature_qiandaosystem.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** CheckInDao 就像是一个打卡记录管理员，负责直接操作打卡记录表和打卡历史记录表。它提供了一系列方法，比如：
 * 添加打卡记录。
 * 查找打卡记录。
 * 更新打卡记录。
 * 删除打卡记录。
 */
@Dao
interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckIn)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCheckIn(checkIn: CheckIn)

    @Query("UPDATE check_in_history SET checkInName = :newName WHERE checkInName = :oldName")
    suspend fun updateCheckInHistoryName(oldName: String, newName: String)

    @Query("SELECT * FROM check_in WHERE name = :name")
    suspend fun getCheckInByName(name: String): CheckIn?

    @Query("SELECT * FROM check_in")
    fun getAllCheckIns(): Flow<List<CheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInHistory(checkInHistory: CheckInHistory)

    @Query("SELECT * FROM check_in_history WHERE checkInName = :checkInName")
    suspend fun getCheckInHistory(checkInName: String): List<CheckInHistory>

    @Query("SELECT COUNT(*) FROM check_in_history WHERE checkInName = :checkInName")
    suspend fun getCheckInCount(checkInName: String): Int

    @Query("SELECT COUNT(*) FROM check_in_history WHERE checkInName = :checkInName AND date = :date")
    suspend fun getCheckInCountByDate(checkInName: String, date: String): Int

    @Query("SELECT * FROM check_in_history WHERE date = :date")
    suspend fun getCheckInHistoryByDate(date: String): List<CheckInHistory>

    @Query("DELETE FROM check_in_history WHERE checkInName = :name")
    suspend fun deleteCheckInHistory(name: String)

    @Query("DELETE FROM check_in WHERE name = :name")
    suspend fun deleteCheckIn(name: String)

    @Query("SELECT * FROM check_in_history WHERE date BETWEEN :start AND :end")
    fun getCheckInHistoryBetweenDates(start: String, end: String): List<CheckInHistory>
}