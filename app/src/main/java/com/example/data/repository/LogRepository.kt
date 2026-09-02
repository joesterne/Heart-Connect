package com.example.data.repository

import com.example.data.database.DailyLogDao
import com.example.data.model.DailyLog
import kotlinx.coroutines.flow.Flow

class LogRepository(private val dailyLogDao: DailyLogDao) {
    val allLogs: Flow<List<DailyLog>> = dailyLogDao.getAllLogs()

    suspend fun insertLog(log: DailyLog) = dailyLogDao.insertLog(log)
    suspend fun insertLogs(logs: List<DailyLog>) = dailyLogDao.insertLogs(logs)

    suspend fun deleteLogById(id: String) = dailyLogDao.deleteLogById(id)
}
