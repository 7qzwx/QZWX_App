package com.qzwx.core.room.room_qiandaosystem

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**CheckInDao 就像是一个 ​打卡记录管理员，负责直接操作打卡记录表和打卡历史记录表。它提供了一系列方法，比如：

​添加​ 打卡记录。
​查找​ 打卡记录。
​更新​ 打卡记录。
​删除​ 打卡记录。
 */
@Dao
interface CheckInDao {
    @Update
    suspend fun updateCheckIn1(checkIn : CheckIn) // 确保这个方法能够接收一个完整的 CheckIn 对象

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn : CheckIn)

    // 新增方法：更新历史记录中的打卡名称
    @Query("UPDATE check_in_history SET checkInName = :newName WHERE checkInName = :oldName")
    suspend fun updateCheckInHistoryName(oldName : String, newName : String)

    @Insert
    suspend fun insert(checkIn : CheckIn)

    @Query("SELECT * FROM check_in WHERE name = :name")
    suspend fun getCheckInByName(name : String) : CheckIn?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCheckIn(checkIn : CheckIn)  // ✅ 统一使用对象更新

    @Query("DELETE FROM check_in WHERE name = :name")
    suspend fun deleteCheckIn(name : String)

    @Query("SELECT * FROM check_in")
    fun getAllCheckIns() : Flow<List<CheckIn>>

    @Insert
    suspend fun insertCheckInHistory(checkInHistory : CheckInHistory)

    @Query("SELECT * FROM check_in_history WHERE checkInName = :checkInName")
    suspend fun getCheckInHistory(checkInName : String) : List<CheckInHistory>

    @Query("SELECT COUNT(*) FROM check_in_history WHERE checkInName = :checkInName")
    suspend fun getCheckInCount(checkInName : String) : Int

    @Query("SELECT COUNT(*) FROM check_in_history WHERE checkInName = :checkInName AND date = :date")
    suspend fun getCheckInCountByDate(checkInName : String, date : String) : Int

    // 新增方法：根据日期获取签到历史记录
    @Query("SELECT * FROM check_in_history WHERE date = :date")
    suspend fun getCheckInHistoryByDate1(date : String) : List<CheckInHistory>

    //删除历史时间记录
    @Query("DELETE FROM check_in_history WHERE checkInName = :name")
    suspend fun deleteCheckInHistory(name : String)

    @Query("SELECT * FROM check_in WHERE name = :name AND lastCheckInDate = :date")
    suspend fun getCheckInByNameAndDate(name : String, date : String) : CheckIn?
}