package com.qzwx.feature_qiandaosystem.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/** CheckInRepositoryImpl 是管家的具体执行者（助手），它实现了 CheckInRepository 中定义的所有任务。
 * 它直接和 Dao 方法（如 CheckInDao）打交道，负责完成具体的任务。
 */
class CheckInRepositoryImpl(private val checkInDao: CheckInDao) : CheckInRepository {
    override suspend fun insertCheckIn(checkIn: CheckIn) {
        checkInDao.insertCheckIn(checkIn)
    }

    override suspend fun getCheckInByName(name: String): CheckIn? {
        return checkInDao.getCheckInByName(name)
    }

    override suspend fun updateCheckIn(checkIn: CheckIn) {
        checkInDao.updateCheckIn(checkIn)
    }

    override suspend fun deleteCheckIn(name: String) {
        checkInDao.deleteCheckIn(name)
    }

    override fun getAllCheckIns(): Flow<List<CheckIn>> {
        return checkInDao.getAllCheckIns()
    }

    override suspend fun insertCheckInHistory(checkInHistory: CheckInHistory) {
        checkInDao.insertCheckInHistory(checkInHistory)
    }

    override suspend fun getCheckInHistory(checkInName: String): List<CheckInHistory> {
        return withContext(Dispatchers.IO) {
            checkInDao.getCheckInHistory(checkInName)
        }
    }

    override suspend fun deleteCheckInHistory(name: String) {
        checkInDao.deleteCheckInHistory(name)
    }

    override suspend fun getCheckInCount(checkInName: String, date: String): Int {
        return checkInDao.getCheckInCountByDate(checkInName, date)
    }

    override suspend fun updateCheckInHistoryName(oldName: String, newName: String) {
        checkInDao.updateCheckInHistoryName(oldName, newName)
    }

    override suspend fun getCheckInHistoryByDate(date: String): List<CheckInHistory> {
        return checkInDao.getCheckInHistoryByDate(date)
    }

    override fun getCheckInHistoryBetweenDates(start: String, end: String): List<CheckInHistory> {
        return checkInDao.getCheckInHistoryBetweenDates(start, end)
    }
}