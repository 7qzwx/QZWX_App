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
    @Query("SELECT * FROM CheckIn")
    suspend fun getAllCheckInsSync() : List<CheckIn>

    @Query("SELECT * FROM CheckInHistory")
    suspend fun getAllCheckInHistoriesSync() : List<CheckInHistory>

    @Query("DELETE FROM CheckIn")
    suspend fun deleteAllCheckIns()

    @Query("DELETE FROM CheckInHistory")
    suspend fun deleteAllCheckInHistories()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCheckIns(entities : List<CheckIn>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCheckInHistories(entities : List<CheckInHistory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn : CheckIn)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCheckIn(checkIn : CheckIn)

    @Query("UPDATE checkinhistory SET checkInName = :newName WHERE checkInName = :oldName")
    suspend fun updateCheckInHistoryName(oldName : String, newName : String)

    @Query("SELECT * FROM checkin WHERE name = :name")
    suspend fun getCheckInByName(name : String) : CheckIn?

    @Query("SELECT * FROM CheckIn")
    fun getAllCheckIns() : Flow<List<CheckIn>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckInHistory(checkInHistory : CheckInHistory)

    @Query("SELECT * FROM checkinhistory WHERE checkInName = :checkInName")
    suspend fun getCheckInHistory(checkInName : String) : List<CheckInHistory>

    @Query("SELECT COUNT(*) FROM checkinhistory WHERE checkInName = :checkInName")
    suspend fun getCheckInCount(checkInName : String) : Int

    @Query("SELECT COUNT(*) FROM checkinhistory WHERE checkInName = :checkInName AND date = :date")
    suspend fun getCheckInCountByDate(checkInName : String, date : String) : Int

    @Query("SELECT * FROM checkinhistory WHERE date = :date")
    suspend fun getCheckInHistoryByDate(date : String) : List<CheckInHistory>

    @Query("DELETE FROM checkinhistory WHERE checkInName = :name")
    suspend fun deleteCheckInHistory(name : String)

    @Query("DELETE FROM checkin WHERE name = :name")
    suspend fun deleteCheckIn(name : String)

    @Query("SELECT * FROM checkinhistory WHERE date BETWEEN :start AND :end")
    fun getCheckInHistoryBetweenDates(start : String, end : String) : List<CheckInHistory>
}