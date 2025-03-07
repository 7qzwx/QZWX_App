package com.qzwx.feature_qiandaosystem.viewmodel

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.qzwx.feature_qiandaosystem.data.CheckIn
import com.qzwx.feature_qiandaosystem.data.CheckInHistory
import com.qzwx.feature_qiandaosystem.data.CheckInRepository
import com.qzwx.feature_qiandaosystem.data.QZXTDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.random.Random

/** CheckInViewModel 是打卡系统的业务逻辑中心，负责处理所有和打卡相关的操作。
 * 它通过 CheckInRepository 操作数据，并将结果提供给 UI 层。
 */
class CheckInViewModel(private val checkInRepository : CheckInRepository) : ViewModel() {
    // 获取所有打卡记录
    val allCheckIns : Flow<List<CheckIn>> = checkInRepository.getAllCheckIns()

    // 插入新的打卡类型
    fun insertCheckIn(name : String) {
        viewModelScope.launch {
            val checkIn = CheckIn(
                name = name,
                days = 0,
                experience = 0,
                level = 1,
                lastCheckInDate = "",
                isLocked = false, // 如果是默认的签到类型，设置为锁定状态
                consecutiveDays = 0
            )
            checkInRepository.insertCheckIn(checkIn)
        }
    }

    // 更新打卡系统的名称
    fun updateCheckInName(oldName : String, newName : String) {
        viewModelScope.launch {
            // 检查新名称是否已存在
            val existingCheckIn = getCheckInByName(newName)
            if (existingCheckIn == null) {
                // 获取旧打卡系统
                val oldCheckIn = getCheckInByName(oldName)
                oldCheckIn?.let {
                    // 更新名称
                    val updatedCheckIn = it.copy(name = newName)
                    // 更新数据库中的打卡系统
                    checkInRepository.updateCheckIn(updatedCheckIn)
                    // 更新历史记录中的打卡名称
                    checkInRepository.updateCheckInHistoryName(oldName, newName)
                }
            } else {
                // 如果新名称已存在，抛出异常或显示错误信息
                throw IllegalArgumentException("打卡类型 '$newName' 已经存在！")
            }
        }
    }

    // 随机生成经验值（1-7）
    fun getRandomExperience() : Int {
        return Random.nextInt(1, 8) // 生成1到7之间的随机数
    }

    // 更新打卡记录
    fun updateCheckIn(checkIn : CheckIn) {
        viewModelScope.launch {
            checkInRepository.updateCheckIn(checkIn)
        }
    }

    // 查询打卡类型是否存在
    suspend fun getCheckInByName(name : String) : CheckIn? {
        return checkInRepository.getCheckInByName(name)
    }

    fun checkIn(name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = LocalDate.now()
            val currentCheckIn = getCheckInByName(name)
            // 计算连续签到天数
            val newConsecutiveDays = if (currentCheckIn != null) {
                val lastDate = if (currentCheckIn.lastCheckInDate.isNullOrBlank()) {
                    LocalDate.now().minusDays(1) // 默认为昨天
                } else {
                    LocalDate.parse(
                        currentCheckIn.lastCheckInDate,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                }
                if (lastDate.isEqual(currentDate.minusDays(1))) currentCheckIn.consecutiveDays + 1 else 1
            } else {
                1
            }
            // 生成随机经验值并计算总经验
            val experience = getRandomExperience()
            val days = (currentCheckIn?.days ?: 0) + 1
            val totalExperience = (currentCheckIn?.experience ?: 0) + experience
            val level = calculateLevel(totalExperience)
            // 创建新的 CheckIn 对象
            val updatedCheckIn = currentCheckIn?.copy(
                days = days,
                experience = totalExperience,
                level = level,
                lastCheckInDate = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                consecutiveDays = newConsecutiveDays
            ) ?: CheckIn(
                name = name,
                days = days,
                experience = totalExperience,
                level = level,
                lastCheckInDate = currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                consecutiveDays = newConsecutiveDays,
                isLocked = false
            )
            // 更新数据库
            updateCheckIn(updatedCheckIn)
            // 插入历史记录
            val currentCount = loadCheckInHistoryCount(name)
            insertCheckInHistory(
                CheckInHistory(
                    checkInName = name,
                    date = currentDate.toString(),
                    experience = experience,
                    checkInCount = currentCount + 1,
                    level = level
                )
            )
        }
    }

    // 新增方法：加载当前用户的打卡历史记录数
    private suspend fun loadCheckInHistoryCount(checkInName : String) : Int {
        return checkInRepository.getCheckInHistory(checkInName).size
    }

    // 计算等级
    private fun calculateLevel(totalExperience : Int) : Int {
        val levelExp = arrayOf(50, 200, 500, 800, 1500, 3000)
        for (i in levelExp.indices) {
            if (totalExperience < levelExp[i]) {
                return i + 1
            }
        }
        return levelExp.size + 1
    }

    // 删除打卡类型
    fun deleteCheckIn(name : String) {
        viewModelScope.launch {
            checkInRepository.deleteCheckIn(name)
            checkInRepository.deleteCheckInHistory(name)
        }
    }

    // 重置打卡记录
    fun resetCheckIn(name : String) {
        viewModelScope.launch {
            // 获取现有数据
            val existingCheckIn = checkInRepository.getCheckInByName(name)
            existingCheckIn?.let {
                // 创建新对象并重置字段
                val resetCheckIn = it.copy(
                    experience = 0,
                    days = 0,
                    level = 1,
                    lastCheckInDate = "",
                    consecutiveDays = 0
                )
                checkInRepository.updateCheckIn(resetCheckIn) // 使用对象更新
                checkInRepository.deleteCheckInHistory(name)
            }
        }
    }

    // 插入打卡历史记录
    fun insertCheckInHistory(checkInHistory : CheckInHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            checkInRepository.insertCheckInHistory(checkInHistory)
        }
    }

    // 检查打卡类型是否存在
    fun checkIfCheckInExists(name : String, onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            val existingCheckIn = getCheckInByName(name)
            onResult(existingCheckIn != null)
        }
    }

    // 切换打卡类型的锁定状态
    fun toggleLockCheckIn(checkInName : String) {
        viewModelScope.launch {
            val checkIn = checkInRepository.getCheckInByName(checkInName)
            checkIn?.let {
                val updated = it.copy(isLocked = !it.isLocked)
                updateCheckIn(updated) // 使用对象更新方法
            }
        }
    }

    private val _checkInHistory = MutableStateFlow<List<CheckInHistory>>(emptyList())
    val checkInHistory : StateFlow<List<CheckInHistory>> = _checkInHistory

    // 新增：加载历史记录的方法
    fun loadCheckInHistory(checkInName : String) {
        viewModelScope.launch(Dispatchers.IO) {
            _checkInHistory.value = checkInRepository.getCheckInHistory(checkInName)
        }
    }

    fun getCheckInHistoryByDate(date : LocalDate) : Flow<List<CheckInHistory>> = flow {
        val history = checkInRepository.getCheckInHistoryByDate(date.toString())
        emit(history)
    }

    // 新增方法：获取签到数据用于热力日历
    suspend fun getCheckInData(startDate : YearMonth, endDate : YearMonth) : Map<LocalDate, Int> {
        return withContext(Dispatchers.IO) {
            val start = startDate.atDay(1).toString() // 转换为 String
            val end = endDate.atEndOfMonth().toString() // 转换为 String
            val checkInHistory = checkInRepository.getCheckInHistoryBetweenDates(start, end)
            checkInHistory.groupingBy { LocalDate.parse(it.date) }
                .eachCount()
                .toMap()
        }
    }

    // 导出功能
    fun exportDatabase(context : Context) {
        viewModelScope.launch {
            QZXTDatabase.exportDatabase(context)
        }
    }

    // 导入功能
    fun importDatabase(context : Context, uri : Uri) {
        viewModelScope.launch {
            QZXTDatabase.importDatabase(context, uri)
        }
    }

    // 启动文件选择器
    fun startFilePicker(filePickerLauncher : ManagedActivityResultLauncher<Array<String>, Uri?>) {
        filePickerLauncher.launch(arrayOf("text/csv"))
    }
}

/**​
 * CheckInViewModelFactory 就像是一个 CheckInViewModel 的生产工厂，
 * 负责根据需求创建 CheckInViewModel 的实例。
 */
class CheckInViewModelFactory(
    private val checkInRepository : CheckInRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass : Class<T>) : T {
        if (modelClass.isAssignableFrom(CheckInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckInViewModel(checkInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
