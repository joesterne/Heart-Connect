package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.DailyLog

@Database(entities = [DailyLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
}
