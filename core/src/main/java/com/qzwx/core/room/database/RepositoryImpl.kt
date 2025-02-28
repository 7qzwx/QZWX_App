package com.qzwx.core.room.database

import com.qzwx.core.room.room_qiandaosystem.CheckIn
import com.qzwx.core.room.room_qiandaosystem.CheckInDao
import com.qzwx.core.room.room_qiandaosystem.CheckInHistory
import com.qzwx.core.room.room_qiandaosystem.CheckInRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

/** CheckInRepositoryImpl 是 ​管家的具体执行者(助手)，它实现了 CheckInRepository 中定义的所有任务。
 *
 * 它直接和 ​Dao方法​（如CheckInDao）打交道，负责完成具体的任务。
 *  */
class CheckInRepositoryImpl(private val checkInDao : CheckInDao) : CheckInRepository {
    override suspend fun insertCheckIn(checkIn : CheckIn) {
        checkInDao.insert(checkIn)
    }

    override suspend fun getCheckInByName(name : String) : CheckIn? {
        return checkInDao.getCheckInByName(name)
    }

    override suspend fun updateCheckIn(
        checkIn : CheckIn
    ) {
        checkInDao.updateCheckIn(checkIn)
    }

    override suspend fun updateCheckIn1(checkIn : CheckIn) {
        checkInDao.updateCheckIn1(checkIn)
    }

    override suspend fun deleteCheckIn(name : String) {
        checkInDao.deleteCheckIn(name)
    }

    override fun getAllCheckIns() : Flow<List<CheckIn>> {
        return checkInDao.getAllCheckIns()
    }

    override suspend fun insertCheckInHistory(checkInHistory : CheckInHistory) {
        checkInDao.insertCheckInHistory(checkInHistory)
    }

    override suspend fun getCheckInHistory(checkInName : String) : List<CheckInHistory> {
        return withContext(Dispatchers.IO) {
            checkInDao.getCheckInHistory(checkInName)
        }
    }

    override suspend fun deleteCheckInHistory(name : String) {
        checkInDao.deleteCheckInHistory(name)
    }

    override suspend fun getCheckInCount(checkInName : String, date : String) : Int {
        return checkInDao.getCheckInCountByDate(checkInName, date)
    }

    // 新增方法：更新历史记录中的打卡名称
    override suspend fun updateCheckInHistoryName(oldName : String, newName : String) {
        checkInDao.updateCheckInHistoryName(oldName, newName)
    }

    // 新增方法：根据日期获取签到历史记录
    override suspend fun getCheckInHistoryByDate1(date: String): List<CheckInHistory> {
        return checkInDao.getCheckInHistoryByDate1(date)
    }
}