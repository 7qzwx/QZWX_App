package com.qzwx.feature_qiandaosystem.data

import kotlinx.coroutines.flow.Flow

/** CheckInRepository 就像一个管家，负责声明所有操作名称。它定义了一系列的任务清单，告诉管家该做什么。比如：
 * 添加打卡记录。
 * 查找打卡记录。
 * 更新打卡记录。
 * 删除打卡记录。
 */
interface CheckInRepository {
    suspend fun insertCheckIn(checkIn: CheckIn)
    suspend fun getCheckInByName(name: String): CheckIn?
    suspend fun updateCheckIn(checkIn: CheckIn)
    suspend fun deleteCheckIn(name: String)
    fun getAllCheckIns(): Flow<List<CheckIn>>
    suspend fun insertCheckInHistory(checkInHistory: CheckInHistory)
    suspend fun getCheckInHistory(checkInName: String): List<CheckInHistory>
    suspend fun deleteCheckInHistory(name: String)
    suspend fun getCheckInCount(checkInName: String, date: String): Int
    suspend fun updateCheckInHistoryName(oldName: String, newName: String)
    suspend fun getCheckInHistoryByDate(date: String): List<CheckInHistory>
    fun getCheckInHistoryBetweenDates(start: String, end: String): List<CheckInHistory>
}