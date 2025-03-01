package com.qzwx.core.room.room_qiandaosystem

import kotlinx.coroutines.flow.Flow

/**CheckInRepository 就像一个 ​管家，负责声明所有操作名称。它定义了一系列的 ​任务清单，告诉管家该做什么。比如：
 *
 * ​添加打卡记录。
 * ​查找打卡记录。
 * ​更新打卡记录。
 * ​删除打卡记录。
 *  */
interface CheckInRepository {
    suspend fun insertCheckIn(checkIn : CheckIn)
    suspend fun getCheckInByName(name : String) : CheckIn?
    suspend fun updateCheckIn(
        checkIn : CheckIn
    )

    suspend fun updateCheckIn1(checkIn : CheckIn)
    suspend fun deleteCheckIn(name : String)
    fun getAllCheckIns() : Flow<List<CheckIn>>
    suspend fun insertCheckInHistory(checkInHistory : CheckInHistory)
    suspend fun getCheckInHistory(checkInName : String) : List<CheckInHistory>
    suspend fun deleteCheckInHistory(name : String)
    suspend fun getCheckInCount(checkInName : String, date : String) : Int

    // 新增方法：更新历史记录中的打卡名称
    suspend fun updateCheckInHistoryName(oldName : String, newName : String)

    // 新增方法：根据日期获取签到历史记录
    suspend fun getCheckInHistoryByDate1(date : String) : List<CheckInHistory>

    // 新增方法：获取指定日期范围内的签到历史记录
    fun getCheckInHistoryBetweenDates(start : String, end : String) : List<CheckInHistory>
}