package com.rishabh.duressstepss.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rishabh.duressstepss.stepcounter.data.local.entity.StepCountEntity

@Database(
    entities = [StepCountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepCountDao(): StepCountDao
}
