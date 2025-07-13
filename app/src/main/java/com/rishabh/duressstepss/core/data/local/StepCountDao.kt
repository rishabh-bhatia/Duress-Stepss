package com.rishabh.duressstepss.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rishabh.duressstepss.stepcounter.data.local.entity.StepCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepCountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCountEntity)

    @Query("SELECT * FROM step_counts ORDER BY timestamp DESC LIMIT 1")
    fun getLatestStepCount(): Flow<StepCountEntity?>
}
